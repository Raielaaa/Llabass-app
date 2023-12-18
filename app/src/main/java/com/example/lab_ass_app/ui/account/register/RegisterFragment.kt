package com.example.lab_ass_app.ui.account.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentRegisterBinding
import com.example.lab_ass_app.ui.Helper

class RegisterFragment : Fragment() {
    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        displayDialog()
        initClickableViews()
        initExistingAccountTV()

        return binding.root
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