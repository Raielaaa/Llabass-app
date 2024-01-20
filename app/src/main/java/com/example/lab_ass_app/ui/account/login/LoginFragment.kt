package com.example.lab_ass_app.ui.account.login

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.preference.PreferenceManager
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentLoginBinding
import com.example.lab_ass_app.utils.`object`.Helper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    // ViewModel and View Binding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding

    // SharedPreferences for User Type
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor

    // SharedPreferences for Google
    private lateinit var sharedPreferencesGoogle: SharedPreferences
    private lateinit var editorGoogle: Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize ViewModel and View Binding
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        loginViewModel = ViewModelProvider(this@LoginFragment)[LoginViewModel::class.java]

        // Initialize SharedPreferences for User Type
        sharedPreferences = requireActivity().getSharedPreferences("UserType_Pref", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        //  Initialize SharedPref
        sharedPreferencesGoogle = requireActivity().getSharedPreferences("GoogleSignIn", Context.MODE_PRIVATE)
        editorGoogle = sharedPreferences.edit()
        sharedPreferencesGoogle.registerOnSharedPreferenceChangeListener(this@LoginFragment)

        // Initialize UI elements and listeners
        initSpinner()
        initNoAccountTextView()
        initLoginButton()
        initPasswordVisibility()
        initGoogleFacebookLogin()

        return binding.root
    }

    private fun initGoogleFacebookLogin() {
        binding.apply {
            ivGoogle.setOnClickListener {
                //  Display a notice dialog during registration
                displayDialog("google")
            }
//            ivFacebook.setOnClickListener {
//                //  Display a notice dialog during registration
//                displayDialog("facebook")
//
//                //  Facebook login
//                Helper.hostFragmentInstanceForFacebookLogin = this@LoginFragment
//            }
        }
    }

    // Initialize the password visibility toggle
    private fun initPasswordVisibility() {
        binding.apply {
            textInputLayoutPassword.setEndIconOnClickListener {
                val passwordType = tilPassword.inputType

                if (passwordType and InputType.TYPE_TEXT_VARIATION_PASSWORD != 0) {
                    // Password input type (visible or not visible)
                    tilPassword.inputType = InputType.TYPE_CLASS_TEXT
                    tilPassword.transformationMethod = null
                    textInputLayoutPassword.setEndIconDrawable(R.drawable.account_show_password_resize)
                } else {
                    // Other input types (not password)
                    tilPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                    tilPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                    textInputLayoutPassword.setEndIconDrawable(R.drawable.account_hide_password_resize)
                }
            }
        }
    }

    // Initialize the login button and trigger the login process
    private fun initLoginButton() {
        binding.apply {
            btnLogin.setOnClickListener {
                loginViewModel.loginUsingFirebase(
                    etEmail,
                    tilPassword,
                    spUser.selectedItem.toString(),
                    this@LoginFragment,
                    editor
                )
            }
        }
    }

    // Initialize the "No Account" TextView and navigate to the registration screen
    private fun initNoAccountTextView() {
        binding.tvNoAccount.text = SpannableStringBuilder()
            .append("Don't have an account? ")
            .color(ContextCompat.getColor(requireContext(), R.color.Theme_color_main)) { append("REGISTER") }

        binding.tvNoAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    // Display a notice dialog during registration
    private fun displayDialog(loginProcess: String) {
        Helper.displayCustomDialog(
            this@LoginFragment,
            R.layout.custom_dialog_notice,
            binding.spUser,
            this@LoginFragment,
            loginViewModel,
            loginProcess
        )
    }

    // Initialize the user type spinner
    private fun initSpinner() {
        binding.apply {
            val spinner: Spinner = spUser
            val spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.account_login_spinner,
                android.R.layout.simple_spinner_dropdown_item
            )

            spinner.adapter = spinnerAdapter
        }
    }

    override fun onSharedPreferenceChanged(sharedPref: SharedPreferences?, key: String?) {
        if (key == "booleanKeyGoogle") {
            Helper.displayCustomDialog(
                requireActivity(),
                R.layout.custom_dialog_loading
            )
            loginViewModel.signInUsingGoogle(requireActivity())
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Register the fragment as a listener for changes in SharedPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Unregister the fragment as a listener when the view is destroyed
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}