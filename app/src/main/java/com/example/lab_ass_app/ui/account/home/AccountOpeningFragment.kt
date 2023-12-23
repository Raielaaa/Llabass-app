package com.example.lab_ass_app.ui.account.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentAccountOpeningBinding

class AccountOpeningFragment : Fragment() {
    private lateinit var viewModel: AccountOpeningViewModel
    private lateinit var binding: FragmentAccountOpeningBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountOpeningBinding.inflate(inflater, container, false)

        binding.apply {
            btnHomeLogin.setOnClickListener {
                findNavController().navigate(R.id.action_accountOpeningFragment_to_loginFragment)
            }
            btnHomeSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_accountOpeningFragment_to_registerFragment)
            }
        }

        if (requireActivity().intent.getBooleanExtra("navigateToHomeFragment", false)) {
            findNavController().navigate(R.id.homeFragment)
        }

        return binding.root
    }
}