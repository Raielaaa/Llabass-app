package com.example.lab_ass_app.ui.account.register

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Message
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentRegisterBinding
import com.example.lab_ass_app.ui.account.register.google_facebook.GoogleDataModel
import com.example.lab_ass_app.ui.account.register.google_facebook.TermsOfServiceDialogFacebook
import com.example.lab_ass_app.ui.account.register.google_facebook.TermsOfServiceDialogGoogle
import com.example.lab_ass_app.utils.Helper
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    //  SharedPref variable
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor

    // ViewModel and View Binding
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize ViewModel and View Binding
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        registerViewModel = ViewModelProvider(this@RegisterFragment)[RegisterViewModel::class.java]

        //  Initialize SharedPref
        sharedPreferences = requireActivity().getSharedPreferences("GoogleSignIn", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this@RegisterFragment)

        // Display a notice dialog during registration
        displayDialog()

        // Initialize UI elements and listeners
        initExistingAccountTextView()
        initSpinner()
        initPasswordVisibility()
        initSignUpButton()

        return binding.root
    }

    // Initialize the sign-up button and trigger the registration process
    private fun initSignUpButton() {
        binding.apply {
            btnSignUp.setOnClickListener {
                registerViewModel.validateEntries(
                    etLRN,
                    etEmail,
                    tilPassword,
                    tilConfirmPassword,
                    this@RegisterFragment,
                    registerViewModel,
                    binding
                )
            }
            ivGoogle.setOnClickListener {
                if (etLRN.text.toString().isNotEmpty()) {
                    TermsOfServiceDialogGoogle(this@RegisterFragment).show(parentFragmentManager, "Register_BottomDialog_Google")
                } else {
                    displayToastMessage("Error: Please provide your LRN for Google/Facebook sign-up.")
                }
            }
            ivFacebook.setOnClickListener {
                if (etLRN.text.toString().isNotEmpty()) {
                    TermsOfServiceDialogFacebook(this@RegisterFragment).show(parentFragmentManager, "Register_BottomDialog_Facebook")
                } else {
                    displayToastMessage("Error: Please provide your LRN for Google/Facebook sign-up.")
                }
            }
        }
    }

    //  Display Toast message
    private fun displayToastMessage(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_LONG
        ).show()
    }

    // Initialize the password visibility toggle
    private fun initPasswordVisibility() {
        binding.apply {
            textInputLayoutPassword.setEndIconOnClickListener {
                togglePasswordVisibility(tilPassword, textInputLayoutPassword)
            }

            textInputLayoutConfirmPassword.setEndIconOnClickListener {
                togglePasswordVisibility(tilConfirmPassword, textInputLayoutConfirmPassword)
            }
        }
    }

    // Function for switching password visibility
    private fun togglePasswordVisibility(
        textInputEditText: TextInputEditText,
        textInputLayout: TextInputLayout
    ) {
        val passwordType = textInputEditText.inputType

        if (passwordType and InputType.TYPE_TEXT_VARIATION_PASSWORD != 0) {
            // Password input type (visible or not visible)
            textInputEditText.inputType = InputType.TYPE_CLASS_TEXT
            textInputEditText.transformationMethod = null
            textInputLayout.setEndIconDrawable(R.drawable.account_show_password_resize)
        } else {
            // Other input types (not password)
            textInputEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            textInputEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            textInputLayout.setEndIconDrawable(R.drawable.account_hide_password_resize)
        }
    }

    // Initialize the user type spinner
    private fun initSpinner() {
        binding.apply {
            val spinner: Spinner = spUserRegister
            val spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.account_register_spinner,
                android.R.layout.simple_spinner_dropdown_item
            )

            spinner.adapter = spinnerAdapter
        }
    }

    // Initialize the "Existing Account" TextView and navigate to the login screen
    private fun initExistingAccountTextView() {
        binding.tvExistingAccount.text = SpannableStringBuilder()
            .append("Already have an account? ")
            .color(ContextCompat.getColor(requireContext(), R.color.Theme_color_main)) { append("LOGIN") }

        binding.tvExistingAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    // Display a notice dialog during registration
    private fun displayDialog() {
        Helper.displayCustomDialog(
            requireActivity(),
            R.layout.custom_dialog_notice
        )
    }

    override fun onSharedPreferenceChanged(sharedPref: SharedPreferences?, key: String?) {
        if (key == "booleanKeyGoogle") {
            Helper.displayCustomDialog(
                requireActivity(),
                R.layout.custom_dialog_loading
            )
            registerViewModel.signInUsingGoogle(requireActivity())

            binding.apply {
                Helper.setUserTypeAndLRNForGoogleSignIn(
                    etLRN.text.toString(),
                    spUserRegister.selectedItem.toString()
                )
            }
        }
    }
}