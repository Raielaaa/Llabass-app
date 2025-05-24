package com.example.lab_ass_app.ui.account.register.otp

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lab_ass_app.R

class VerifyOTPFragment : Fragment() {

    companion object {
        fun newInstance() = VerifyOTPFragment()
    }

    private val viewModel: VerifyOTViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_verify_o_t, container, false)
    }
}