package com.example.lab_ass_app.ui.account.register

import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class RegisterViewModel @Inject constructor(
    @Named("FirebaseAuth.Instance")
    val firebaseAuth: FirebaseAuth
) : ViewModel() {

    //  Function for validating/inserting inputted data to firebase auth
    fun signUpUser(
        etLRN: EditText,
        etEmail: EditText,
        tilPassword: TextInputEditText,
        tilConfirmPassword: TextInputEditText,
        registerFragment: RegisterFragment
    ) {
        val lrn = etLRN.text.toString()
        val email = etEmail.text.toString()
        val password = tilPassword.text.toString()
        val confirmPassword = tilConfirmPassword.text.toString()

        if (lrn.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (password == confirmPassword) {
                //  Insert the credentials to firebase authentication
                insertDataToAuth(
                    etLRN,
                    etEmail,
                    tilPassword,
                    tilConfirmPassword,
                    registerFragment
                )
            } else {
                //  Sets the password and confirm password field to empty if it
                //  does not match
                tilPassword.setText("")
                tilConfirmPassword.setText("")
                displayToastMessage("Error: Password and Confirm password do not match", registerFragment)
            }
        } else {
            //  Displays if there is an empty field
            displayToastMessage("All fields are required", registerFragment)
        }
    }

    //  Function for displaying toast on the screen
    private fun displayToastMessage(message: String, registerFragment: RegisterFragment) {
        Toast.makeText(
            registerFragment.requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun insertDataToAuth(
        etLRN: EditText,
        etEmail: EditText,
        tilPassword: TextInputEditText,
        tilConfirmPassword: TextInputEditText,
        registerFragment: RegisterFragment
    ) {
        firebaseAuth.createUserWithEmailAndPassword(etEmail.text.toString(), tilPassword.text.toString())
    }
    // TODO: Implement the ViewModel
}