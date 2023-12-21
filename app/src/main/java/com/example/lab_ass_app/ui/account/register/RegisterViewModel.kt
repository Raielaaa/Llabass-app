package com.example.lab_ass_app.ui.account.register

import android.app.Activity
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.utils.Constants
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentRegisterBinding
import com.example.lab_ass_app.utils.Helper
import com.example.lab_ass_app.ui.account.register.user_account_initial.UserAccountInitialModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class RegisterViewModel @Inject constructor(
    @Named("FirebaseAuth.Instance")
    val firebaseAuth: FirebaseAuth,
    @Named("FirebaseFireStore.Instance")
    val firebaseFireStore: FirebaseFirestore
) : ViewModel() {
    // Function for displaying toast messages on the screen
    private fun displayToastMessage(message: String, registerFragment: RegisterFragment) {
        Toast.makeText(
            registerFragment.requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    // Insert User credentials to Firebase auth and FireStore
    fun insertDataToAuth(
        lrn: String,
        email: String,
        password: String,
        userType: String,
        registerFragment: RegisterFragment
    ) {
        // Display loading dialog
        Helper.displayCustomDialog(
            registerFragment.requireActivity(),
            R.layout.custom_dialog_loading
        )

        // Create user for authentication
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userID = task.result.user?.uid

                    // Storing user credentials to UserAccountInitial data class
                    val userAccountInitial = UserAccountInitialModel(
                        userID.toString(),
                        lrn,
                        email,
                        userType
                    )

                    // Insert UserID, LRN, Email, and UserType to FireStore
                    firebaseFireStore.collection("labass-app-user-account-initial")
                        .document(userID ?: "Error: UserID not found")
                        .set(userAccountInitial)
                        .addOnSuccessListener {
                            // If success
                            displayToastMessage("Register successful", registerFragment)

                            // Navigate to LoginFragment
                            registerFragment.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)

                            // Dismiss dialog
                            Helper.dismissDialog()
                        }.addOnFailureListener { exception ->
                            // If failed
                            displayToastMessage("Error: ${exception.localizedMessage}", registerFragment)
                            Log.e(Constants.TAG, "insertDataToAuth: ${exception.message}")

                            // Dismiss dialog
                            Helper.dismissDialog()
                        }
                }
            }.addOnFailureListener { exception ->
                displayToastMessage("Error: ${exception.localizedMessage}", registerFragment)
                Log.d(Constants.TAG, "insertDataToAuth: ${exception.message}")

                // Dismiss dialog
                Helper.dismissDialog()
            }
    }

    // Function for validating/inserting inputted data to Firebase auth
    fun validateEntries(
        etLRN: EditText,
        etEmail: EditText,
        tilPassword: TextInputEditText,
        tilConfirmPassword: TextInputEditText,
        registerFragment: RegisterFragment,
        registerViewModel: RegisterViewModel,
        registerBinding: FragmentRegisterBinding
    ) {
        val lrn = etLRN.text.toString()
        val email = etEmail.text.toString()
        val password = tilPassword.text.toString()
        val confirmPassword = tilConfirmPassword.text.toString()

        // Checks if LRN, Email, Password, and Confirm password are not empty
        if (lrn.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            // Checks if Password matches with Confirm password
            if (password == confirmPassword) {
                // Show Terms of Service Dialog for confirmation
                TermsOfServiceDialog(
                    registerFragment,
                    registerViewModel,
                    registerBinding
                ).show(registerFragment.parentFragmentManager, "Register_BottomDialog")
            } else {
                // Sets the password and confirm password field to empty if they do not match
                tilPassword.setText("")
                tilConfirmPassword.setText("")
                displayToastMessage("Error: Password and Confirm password do not match", registerFragment)
            }
        } else {
            // Displays if there is an empty field
            displayToastMessage("All fields are required", registerFragment)
        }
    }
}