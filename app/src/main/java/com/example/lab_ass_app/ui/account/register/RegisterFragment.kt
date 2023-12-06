package com.example.lab_ass_app.ui.account.register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        return binding.root
    }

    private fun displayDialog() {
        Helper.displayCustomDialog(
            this@RegisterFragment,
            R.layout.custom_dialog_notice
        )
    }
}