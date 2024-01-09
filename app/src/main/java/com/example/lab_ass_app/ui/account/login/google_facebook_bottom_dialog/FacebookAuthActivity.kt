package com.example.lab_ass_app.ui.account.login.google_facebook_bottom_dialog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lab_ass_app.main.MainActivity
import com.example.lab_ass_app.R
import com.example.lab_ass_app.ui.account.register.google_facebook.FacebookGoogleDataModel
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.`object`.Helper
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider

@Suppress("DEPRECATION")
class FacebookAuthActivity() : MainActivity() {

    private lateinit var callbackManager: CallbackManager
    private val hostFragment: Fragment? by lazy { Helper.hostFragmentInstanceForFacebookLogin }
    private var navigateToHomeFragment: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callbackManager = CallbackManager.Factory.create()
        navigateToHomeFragment = intent.getBooleanExtra("navigateToHomeFragment", false)

        LoginManager.getInstance().logInWithReadPermissions(this@FacebookAuthActivity, listOf("public_profile", "email"))
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                Log.e(Constants.TAG, "FacebookOnCancel")
            }

            override fun onError(error: FacebookException) {
                Log.e(Constants.TAG, "FacebookOnError: ${error.message}")
            }

            override fun onSuccess(result: LoginResult) {
                Log.d(Constants.TAG, "facebook:onSuccess:$result")
                handleFacebookAccessToken(result.accessToken)
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Helper.displayCustomDialog(
            this@FacebookAuthActivity,
            R.layout.custom_dialog_loading
        )

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(Constants.TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser

                    // Get information from the GoogleSignInAccount
                    val email = user?.email
                    val displayName = user?.displayName
                    val uid = user?.uid

                    // Insert data from Google sign-in to FireStore
                    val dataToBeInserted = FacebookGoogleDataModel(email!!, uid!!, Helper.lrn, Helper.userType)
                    val userID = task.result.user!!.uid

                    facebookSignInAccountValidation(email, userID, dataToBeInserted, displayName.toString())
                } else {
                    // If sign in fails, display a message to the user.
                    endTaskNotify(task.exception!!)
                }
            }
    }

    private fun facebookSignInAccountValidation(
        email: String,
        userID: String,
        dataToBeInserted: FacebookGoogleDataModel,
        displayName: String
    ) {
        firebaseFireStore.collection("labass-app-user-account-initial")
            .whereEqualTo("userEmailModel", email)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents = task.result

                    if (documents != null && !documents.isEmpty) {
                        // Assuming there is only one document, you can access its fields
                        val document = documents.documents[0]

                        // Accessing the values
                        val userEmailModel = document.getString("userEmailModel")
                        val userLRNModel = document.getString("userLRNModel")
                        val userTypeModel = document.getString("userTypeModel")

                        if (userEmailModel == email && userLRNModel == Helper.lrn && userTypeModel == Helper.userType) {
                            // Display a message or use the information as needed
                            displayToastMessage("Signed in as $displayName (Email: $email)")
                            displayToastMessage("Facebook sign-in successful")

                            navigateToHomePage()
                            Helper.dismissDialog()
                        } else {
                            //  Existing account's other info does not match with info on the server
                            Helper.displayCustomDialog(
                                this@FacebookAuthActivity,
                                R.layout.custom_dialog_not_found,
                                applicationContext
                            )
                        }
                    } else {
                        // Document not found, insert the data
                        insertFacebookDataToFireStore(userID, dataToBeInserted)
                    }
                }
            }
    }

    private fun insertFacebookDataToFireStore(userID: String, data: FacebookGoogleDataModel) {
        firebaseFireStore.collection("labass-app-user-account-initial")
            .document(userID)
            .set(data)
            .addOnSuccessListener {
                displayToastMessage("Facebook sign-in successful")
                navigateToHomePage()
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
        Log.e(Constants.TAG, "Error-endTaskNotify: ${exception.message}")
        Helper.dismissDialog()
    }

    // Display toast message
    private fun displayToastMessage(message: String) {
        Toast.makeText(
            this@FacebookAuthActivity,
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun navigateToHomePage() {
        val intent: Intent = Intent(this@FacebookAuthActivity, MainActivity::class.java)
        intent.putExtra("navigateToHomeFragment", true)
        startActivityForResult(intent, 123)
    }

    private fun displayToastMessage(message: String, hostFragment: Fragment) {
        Toast.makeText(
            hostFragment.requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}