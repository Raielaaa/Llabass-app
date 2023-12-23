package com.example.lab_ass_app.ui.account.login.google_facebook_bottom_dialog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.MainActivity
import com.example.lab_ass_app.R
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.utils.Helper
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import java.util.Arrays

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
//                displayToastMessage("Facebook cancel", hostFragment)
                Log.e(Constants.TAG, "FacebookOnCancel")
            }

            override fun onError(error: FacebookException) {
//                displayToastMessage("Facebook cancel", hostFragment)
                Log.e(Constants.TAG, "FacebookOnError: ${error.message}")
            }

            override fun onSuccess(result: LoginResult) {
//                displayToastMessage("Facebook cancel", hostFragment)

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
        Log.d(Constants.TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(Constants.TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    displayToastMessage("Facebook Authentication Success. Signed in as $user", hostFragment!!)

                    val intent: Intent = Intent(this@FacebookAuthActivity, MainActivity::class.java)
                    intent.putExtra("navigateToHomeFragment", true)
                    startActivityForResult(intent, 123)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(Constants.TAG, "signInWithCredential:failure", task.exception)
                    displayToastMessage("Facebook Authentication Failed", hostFragment!!)
                }
            }
    }

    private fun displayToastMessage(message: String, hostFragment: Fragment) {
        Toast.makeText(
            hostFragment.requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun updateUI(message: String) {
        Log.d(Constants.TAG, "signInWithCredential-$message: updateUI")
    }
}