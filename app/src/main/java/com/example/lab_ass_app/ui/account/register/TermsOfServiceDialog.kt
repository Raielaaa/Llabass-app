package com.example.lab_ass_app.ui.account.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.lab_ass_app.databinding.FragmentRegisterBinding
import com.example.lab_ass_app.databinding.FragmentRegisterTermsOfServiceBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TermsOfServiceDialog(
    private val hostFragment: RegisterFragment,
    private val registerViewModel: RegisterViewModel,
    private val registerBinding: FragmentRegisterBinding
) : BottomSheetDialogFragment() {

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
                // Insert user data to Firebase FireStore and navigate to the next screen
                registerViewModel.insertDataToAuth(
                    registerBinding.etLRN.text.toString(),
                    registerBinding.etEmail.text.toString(),
                    registerBinding.tilPassword.text.toString(),
                    registerBinding.spUserRegister.selectedItem.toString(),
                    hostFragment
                )

                // Dismiss the dialog
                this@TermsOfServiceDialog.dismiss()
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
                this@TermsOfServiceDialog.dismiss()
            }
        }
    }
}