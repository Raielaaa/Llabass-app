package com.example.lab_ass_app.ui.account.register.google_facebook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.lab_ass_app.databinding.FragmentRegisterBinding
import com.example.lab_ass_app.databinding.FragmentRegisterTermsOfServiceBinding
import com.example.lab_ass_app.ui.account.register.RegisterFragment
import com.example.lab_ass_app.ui.account.register.RegisterViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TermsOfServiceDialogFacebook(private val hostFragment: RegisterFragment) : BottomSheetDialogFragment() {
    // View Binding
    private lateinit var binding: FragmentRegisterTermsOfServiceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterTermsOfServiceBinding.inflate(inflater, container, false)

        // Set up click listeners for accept and decline buttons
        setupButtonListeners()

        return binding.root
    }

    // Set up click listeners for accept and decline buttons
    private fun setupButtonListeners() {
        binding.apply {
            // Accept button click listener
            btnAccept.setOnClickListener {

                // Dismiss the dialog
                this@TermsOfServiceDialogFacebook.dismiss()
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
                this@TermsOfServiceDialogFacebook.dismiss()
            }
        }
    }
}