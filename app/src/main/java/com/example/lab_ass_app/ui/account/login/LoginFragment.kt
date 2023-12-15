package com.example.lab_ass_app.ui.account.login

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.core.text.color
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
        initNoAccountTV()

        return binding.root
    }

    private fun initNoAccountTV() {
        binding.tvNoAccount.text = SpannableStringBuilder()
            .append("Don't have an account? ")
            .color(ContextCompat.getColor(requireContext(), R.color.Theme_color_main)) { append("REGISTER") }
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