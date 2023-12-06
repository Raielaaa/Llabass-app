package com.example.lab_ass_app.ui.account.login

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        initSpinner()

        return binding.root
    }

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
}