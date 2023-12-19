package com.example.lab_ass_app.ui.account.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentRegisterBinding
import com.example.lab_ass_app.ui.Helper
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        registerViewModel = ViewModelProvider(this@RegisterFragment)[RegisterViewModel::class.java]

        displayDialog()
        initClickableViews()
        initExistingAccountTV()
        initSpinner()
        initPasswordVisibility()
        initSignUpButton()

        return binding.root
    }

    private fun initSignUpButton() {
        binding.apply {
            registerViewModel.signUpUser(
                etLRN,
                etEmail,
                tilPassword,
                tilConfirmPassword,
                this@RegisterFragment
            )
        }
    }

    private fun initPasswordVisibility() {
        binding.apply {
            textInputLayoutPassword.setEndIconOnClickListener {
                iniTextInputEditText(tilPassword, textInputLayoutPassword)
            }

            textInputLayoutConfirmPassword.setEndIconOnClickListener {
                iniTextInputEditText(tilConfirmPassword, textInputLayoutConfirmPassword)
            }
        }
    }

    private fun iniTextInputEditText(
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

    private fun initExistingAccountTV() {
        binding.tvExistingAccount.text = SpannableStringBuilder()
            .append("Already have an account? ")
            .color(ContextCompat.getColor(requireContext(), R.color.Theme_color_main)) { append("LOGIN") }

        binding.tvExistingAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun initClickableViews() {
        binding.apply {
            btnSignUp.setOnClickListener {
                TermsOfServiceDialog(this@RegisterFragment).show(parentFragmentManager, "Register_BottomDialog")
            }
        }
    }

    private fun displayDialog() {
        Helper.displayCustomDialog(
            this@RegisterFragment,
            R.layout.custom_dialog_notice
        )
    }
}