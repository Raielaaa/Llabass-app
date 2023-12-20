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
    private lateinit var binding: FragmentRegisterTermsOfServiceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterTermsOfServiceBinding.inflate(inflater, container, false)

        //  Inserting values to Firebase FireStore
        binding.btnAccept.setOnClickListener {
            registerViewModel.insertDataToAuth(
                registerBinding.etLRN.text.toString(),
                registerBinding.etEmail.text.toString(),
                registerBinding.tilPassword.text.toString(),
                registerBinding.spUserRegister.selectedItem.toString(),
                hostFragment
            )

            this.dismiss()
        }

        binding.btnDecline.setOnClickListener {
            Toast.makeText(
                hostFragment.requireContext(),
                "Acceptance of terms and conditions is required to proceed.",
                Toast.LENGTH_LONG
            ).show()

            this.dismiss()
        }

        return binding.root
    }
}