package com.example.lab_ass_app

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.example.lab_ass_app.databinding.ActivityMainBinding
import com.example.lab_ass_app.ui.account.register.google_facebook.GoogleDataModel
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.Helper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    //  Firebase auth
    @Inject
    @Named("FirebaseAuth.Instance")
    lateinit var firebaseAuth: FirebaseAuth

    //  Firebase fireStore
    @Inject
    @Named("FirebaseFireStore.Instance")
    lateinit var firebaseFireStore: FirebaseFirestore

    // View Binding and NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    // Bitmap for QR code
    private var imageBitmap: Bitmap? = null

    //  SharedPref
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //  Initialize SharedPref
        sharedPreferences = getSharedPreferences("GoogleSignIn", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        //  Initialize navigation host fragment and navigation drawer
        initNavHostFragment()
        initNavDrawer()
    }

    //  Initialize navigation drawer
    private fun initNavDrawer() {
        binding.apply {
            // Set up instances for DrawerLayout and NavigationView in Helper class
            Helper.drawerLayoutInstance = drawerLayout
            Helper.navDrawerInstance = navDrawer

            // Set navigation item click listener
            navDrawer.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_home -> navigate(R.id.homeFragment, R.id.nav_home)
                    R.id.nav_list -> navigate(R.id.listFragment, R.id.nav_list)
                    R.id.nav_user -> navigate(R.id.profileFragment, R.id.nav_user)
                    R.id.nav_privacy -> navigate(R.id.privacyFragment, R.id.nav_privacy)
                }
                true
            }
        }
    }

    // Navigate to the selected fragment with custom animations
    private fun navigate(fragment: Int, drawerItem: Int) {
        binding.apply {
            drawerLayout.closeDrawer(GravityCompat.START)
            navDrawer.setCheckedItem(drawerItem)

            CoroutineScope(Dispatchers.IO).launch {
                delay(500)

                withContext(Dispatchers.Main) {
                    navController.navigate(
                        fragment,
                        null,
                        getCustomNavOptions(R.anim.fade_in, R.anim.fade_out)
                    )
                }
            }
        }
    }

    // Define custom navigation options with animations
    private fun getCustomNavOptions(enterAnim: Int, exitAnim: Int): NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(enterAnim)
            .setExitAnim(exitAnim)
            .build()
    }

    // Initialize navigation host fragment
    private fun initNavHostFragment() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.navController
    }

    // Handle result from Google/Facebook sign-in and QR code scanner
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.CAMERA_PERMISSION_CODE) {
            val extras: Bundle? = data?.extras

            try {
                // Retrieve image bitmap from extras
                imageBitmap = extras?.get("data") as Bitmap

                // Display QR code dialog
                Helper.displayBorrowReturnDialog(
                    supportFragmentManager,
                    imageBitmap,
                    this@MainActivity
                )
            } catch (err: Exception) {
                // Handle errors
                displayToastMessage("An error occurred: ${err.localizedMessage}")
                return
            }
        }

        if (requestCode == Constants.GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)

                // Save Google Sign-In information to SharedPreferences
                saveGoogleSignInInfo(account.idToken!!)
            } catch (exception: Exception) {
                Helper.dismissDialog()
                endTaskNotify(exception)
            }
        }
    }

    // Function to save Google Sign-In information to SharedPreferences
    private fun saveGoogleSignInInfo(idToken: String) {
        editor.putString("GoogleSignIn_Token", idToken)
        editor.apply()
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this@MainActivity) { task ->
                if (task.isSuccessful) {
                    //  Displaying the current user
                    val user = firebaseAuth.currentUser

                    // Get information from the GoogleSignInAccount
                    val email = user?.email
                    val displayName = user?.displayName
                    val uid = user?.uid

                    // Display a message or use the information as needed
                    displayToastMessage("Signed in as $displayName (Email: $email)")

                    // Insert data from Google sign-in to FireStore
                    val dataToBeInserted = GoogleDataModel(email!!, uid!!, Helper.lrn, Helper.userType)
                    val userID = task.result.user!!.uid
                    insertGoogleDataToFireStore(userID, dataToBeInserted)
                } else {
                    displayToastMessage("Authentication failed")
                    Helper.dismissDialog()
                }
            }
    }

    private fun insertGoogleDataToFireStore(userID: String, data: GoogleDataModel) {
        firebaseFireStore.collection("labass-app-user-account-initial")
            .document(userID)
            .set(data)
            .addOnSuccessListener {
                displayToastMessage("Google sign-in successful")
                navController.navigate(R.id.action_loginFragment_to_homeFragment)
                Helper.dismissDialog()
            }.addOnFailureListener { exception ->
                Helper.dismissDialog()
                endTaskNotify(exception)
            }
    }

    // Function to handle the end of tasks and notify the user about errors
    private fun endTaskNotify(exception: Exception) {
        // Display an error message, log the exception, and dismiss the loading dialog
        displayToastMessage("Error: ${exception.localizedMessage}")
        Log.e(Constants.TAG, "isCredentialsWithUserTypeExist: ${exception.message}")
        Helper.dismissDialog()
    }

    // Display toast message
    private fun displayToastMessage(message: String) {
        Toast.makeText(
            this@MainActivity,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}