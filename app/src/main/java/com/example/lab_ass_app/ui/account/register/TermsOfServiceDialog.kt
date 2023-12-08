package com.example.lab_ass_app.ui.account.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentRegisterTermsOfServiceBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TermsOfServiceDialog(
    private val hostFragment: Fragment
) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentRegisterTermsOfServiceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterTermsOfServiceBinding.inflate(inflater, container, false)

        binding.btnAccept.setOnClickListener {
            hostFragment.findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
            this.dismiss()
        }

        return binding.root
    }
}