package com.example.lab_ass_app.ui.account.login

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
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
import androidx.core.os.bundleOf
import androidx.core.text.color
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    //  General
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding

    //  SharedPref
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Init General
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        //  Init SharedPref
        sharedPreferences = requireActivity().getSharedPreferences("UserType_Pref", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        initSpinner()
        initNoAccountTV()
        initLoginButton()

        return binding.root
    }

    private fun initLoginButton() {
        binding.apply {
            btnLogin.setOnClickListener {
                when (spUser.selectedItem) {
                    "ADMIN" -> {
                        findNavController().navigate(R.id.action_loginFragment_to_homeAdminFragment2)
                    }
                    "STUDENT" -> {
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment, bundleOf(Pair("user_type", "STUDENT")))
                    }
                    "TEACHER" -> {
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment, bundleOf(Pair("user_type", "TEACHER")))
                    }
                }

                editor.apply {
                    putString("user_type", spUser.selectedItem.toString())
                    commit()
                }
            }
        }
    }

    private fun initNoAccountTV() {
        binding.tvNoAccount.text = SpannableStringBuilder()
            .append("Don't have an account? ")
            .color(ContextCompat.getColor(requireContext(), R.color.Theme_color_main)) { append("REGISTER") }

        binding.tvNoAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
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