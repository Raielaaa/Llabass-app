package com.example.lab_ass_app.ui.account.login

import android.app.Activity
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentLoginBinding
import com.example.lab_ass_app.ui.account.register.google_facebook.TermsOfServiceDialogGoogle
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.Helper
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class LoginViewModel @Inject constructor(
    @Named("FirebaseAuth.Instance")
    val firebaseAuth: FirebaseAuth,
    @Named("FirebaseFireStore.Instance")
    val firebaseFireStore: FirebaseFirestore,
    @Named("GoogleSignInClient.Instance")
    val googleSignInClient: GoogleSignInClient
) : ViewModel() {
    // Function to initiate the login process using Firebase
    fun loginUsingFirebase(
        etEmail: EditText,
        tilPassword: TextInputEditText,
        userType: String,
        hostFragment: Fragment,
        editor: Editor
    ) {
        val email: String = etEmail.text.toString()
        val password: String = tilPassword.text.toString()

        // Check if email and password are not empty before proceeding
        if (email.isNotEmpty() && password.isNotEmpty()) {
            // Perform credentials validation
            isCredentialsWithUserTypeExist(email, password, userType, hostFragment, editor)
        } else {
            // Display a message if any field is empty
            displayToastMessage("All fields are required", hostFragment)
        }
    }

    // Function for checking whether the account exists or not
    private fun isCredentialsWithUserTypeExist(
        email: String,
        password: String,
        userType: String,
        hostFragment: Fragment,
        editor: Editor
    ) {
        // Display a loading dialog during the validation process
        Helper.displayCustomDialog(hostFragment.requireActivity(), R.layout.custom_dialog_loading)
        try {
            // Query FireStore to check if the account exists
            firebaseFireStore.collection("labass-app-user-account-initial")
                .whereEqualTo("userEmailModel", email)
                .whereEqualTo("userTypeModel", userType)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result
                        if (!documents.isEmpty && documents != null) {
                            // Documents found, account found; validate credentials with Firebase Auth
                            loginToAuth(email, password, userType, hostFragment, editor)
                        } else {
                            // No documents found, account not found
                            Helper.dismissDialog()
                            displayToastMessage("Account not found, please try again", hostFragment)
                        }
                    } else {
                        // Task not successful
                        endTaskNotify(task.exception!!, hostFragment)
                    }
                }.addOnFailureListener { exception ->
                    endTaskNotify(exception, hostFragment)
                }
        } catch (err: Exception) {
            // Handle general exceptions
            endTaskNotify(err, hostFragment)
        }
    }

    // Function to handle Firebase authentication after successful validation
    private fun loginToAuth(
        email: String,
        password: String,
        userType: String,
        hostFragment: Fragment,
        editor: Editor
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Navigate to the appropriate screen based on the user type
                    handleNavigation(userType, hostFragment, editor)
                }
            }.addOnFailureListener { exception ->
                // Handle authentication failure
                endTaskNotify(exception, hostFragment)
            }
    }

    private fun handleNavigation(userType: String, hostFragment: Fragment, editor: Editor) {
        when (userType) {
            "ADMIN" -> {
                hostFragment.findNavController().navigate(R.id.action_loginFragment_to_homeAdminFragment2)
            }
            "STUDENT" -> {
                hostFragment.findNavController().navigate(R.id.action_loginFragment_to_homeFragment, bundleOf(Pair("user_type", "STUDENT")))
                insertUserTypeToSharedPref(userType, editor)
            }
            "TEACHER" -> {
                hostFragment.findNavController().navigate(R.id.action_loginFragment_to_homeFragment, bundleOf(Pair("user_type", "TEACHER")))
                insertUserTypeToSharedPref(userType, editor)
            }
        }
        // Dismiss the loading dialog and display a success message
        Helper.dismissDialog()
        displayToastMessage("Login successful", hostFragment)
    }

    // Function to insert the user type into shared preferences
    private fun insertUserTypeToSharedPref(userType: String, editor: Editor) {
        editor.apply {
            putString("user_type", userType)
            commit()
        }
    }

    // Function to handle the end of tasks and notify the user about errors
    private fun endTaskNotify(exception: Exception, hostFragment: Fragment) {
        // Display an error message, log the exception, and dismiss the loading dialog
        displayToastMessage("Error: ${exception.localizedMessage}", hostFragment)
        Log.e(Constants.TAG, "isCredentialsWithUserTypeExist: ${exception.message}")
        Helper.dismissDialog()
    }

    // Function to display toast messages
    private fun displayToastMessage(message: String, hostFragment: Fragment) {
        Toast.makeText(
            hostFragment.requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    fun loginViaGoogle(loginFragment: LoginFragment, lrn: String, loginProcess: String) {
        if (lrn.isNotEmpty()) {
            TermsOfServiceDialogGoogle(loginFragment, loginProcess).show(loginFragment.parentFragmentManager, "Register_BottomDialog_Google")
        } else {
            displayToastMessage("Error: Please provide your LRN for Google/Facebook sign-up.", loginFragment)
        }
    }

    fun signInUsingGoogle(activity: Activity) {
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, Constants.GOOGLE_SIGN_IN)
    }

    fun facebookLogin(binding: FragmentLoginBinding, hostFragment: Fragment) {
        //  Initialize Facebook login button
        val callbackManager = CallbackManager.Factory.create()

        binding.ivFacebook.apply {
            setPermissions("email", "public_profile")
            registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onCancel() {
                        displayToastMessage("Facebook cancel", hostFragment)
                        Log.e(Constants.TAG, "FacebookOnCancel")
                    }

                    override fun onError(error: FacebookException) {
                        displayToastMessage("Facebook error:  ${error.localizedMessage}", hostFragment)
                        Log.e(Constants.TAG, "FacebookOnError: ${error.message}")
                    }

                    override fun onSuccess(result: LoginResult) {
                        Log.d(Constants.TAG, "facebook:onSuccess:$result")
                        handleFacebookAccessToken(result.accessToken, hostFragment)
                    }
                }
            )
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken, hostFragment: Fragment) {
        Log.d(Constants.TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(Constants.TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    displayToastMessage("Facebook Authentication Success. Signed in as $user", hostFragment)
                    hostFragment.findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(Constants.TAG, "signInWithCredential:failure", task.exception)
                    displayToastMessage("Facebook Authentication Failed", hostFragment)
                }
            }
    }
}