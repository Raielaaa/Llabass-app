package com.example.lab_ass_app.ui.account.register.google_facebook

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.lab_ass_app.databinding.FragmentRegisterTermsOfServiceBinding
import com.example.lab_ass_app.ui.account.login.LoginFragment
import com.example.lab_ass_app.ui.account.login.LoginViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TermsOfServiceDialogGoogle(
    private val hostFragment: LoginFragment,
    private val loginProcess: String,
    private val loginViewModel: LoginViewModel
) : BottomSheetDialogFragment() {
    //  View Binding
    private lateinit var binding: FragmentRegisterTermsOfServiceBinding

    //  SharedPref
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //  Inflate the layout for this fragment
        binding = FragmentRegisterTermsOfServiceBinding.inflate(inflater, container, false)

        //  Setup SharedPref variables
        sharedPreferences = requireParentFragment().requireActivity().getSharedPreferences("GoogleSignIn", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // Set up click listeners for accept and decline buttons
        setupButtonListeners()

        return binding.root
    }

    // Set up click listeners for accept and decline buttons
    private fun setupButtonListeners() {
        binding.apply {
            // Accept button click listener
            btnAccept.setOnClickListener {
                when (loginProcess) {
                    "google" -> {
                        editor.apply {
                            putBoolean("booleanKeyGoogle", !sharedPreferences.getBoolean("booleanKeyGoogle", true))
                            commit()
                        }
                    }
                    "facebook" -> {
                        loginViewModel.facebookLogin(hostFragment)
                    }
                    else -> {
                        Toast.makeText(
                            hostFragment.requireContext(),
                            "Unknown login process",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                // Dismiss the dialog
                this@TermsOfServiceDialogGoogle.dismiss()
            }

            // Decline button click listener
            btnDecline.setOnClickListener {
                // Show a toast message indicating that acceptance is required
                Toast.makeText(
                    hostFragment.requireContext(),
                    "Acceptance of terms and conditions is required to proceed.",
                    Toast.LENGTH_LONG
                ).show()

                // Dismiss the dialog
                this@TermsOfServiceDialogGoogle.dismiss()
            }
        }
    }
}