package com.example.lab_ass_app.ui.account.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lab_ass_app.databinding.FragmentRegisterTermsOfServiceBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TermsOfServiceDialog : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentRegisterTermsOfServiceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterTermsOfServiceBinding.inflate(inflater, container, false)



        return binding.root
    }
}