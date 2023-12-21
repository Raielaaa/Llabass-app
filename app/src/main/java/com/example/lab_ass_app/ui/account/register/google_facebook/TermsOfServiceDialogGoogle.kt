package com.example.lab_ass_app.ui.account.register.google_facebook

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentRegisterBinding
import com.example.lab_ass_app.databinding.FragmentRegisterTermsOfServiceBinding
import com.example.lab_ass_app.ui.account.login.LoginFragment
import com.example.lab_ass_app.ui.account.register.RegisterFragment
import com.example.lab_ass_app.ui.account.register.RegisterViewModel
import com.example.lab_ass_app.utils.Helper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TermsOfServiceDialogGoogle(
    private val hostFragment: LoginFragment,
    private val loginProcess: String
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
                if (loginProcess == "google") {
                    editor.apply {
                        putBoolean("booleanKeyGoogle", !sharedPreferences.getBoolean("booleanKeyGoogle", true))
                        commit()
                    }
                } else if (loginProcess == "facebook") {
                    Toast.makeText(
                        hostFragment.requireContext(),
                        "Facebook process clicked",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        hostFragment.requireContext(),
                        "Unknown login process",
                        Toast.LENGTH_LONG                    ).show()
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