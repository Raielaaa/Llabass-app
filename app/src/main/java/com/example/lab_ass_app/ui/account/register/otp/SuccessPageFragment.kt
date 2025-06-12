package com.example.lab_ass_app.ui.account.register.otp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentSuccessPageBinding
import com.example.lab_ass_app.ui.account.register.TermsOfServiceDialog
import com.example.lab_ass_app.utils.`object`.DataCache

class SuccessPageFragment : Fragment() {
    private lateinit var binding: FragmentSuccessPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSuccessPageBinding.inflate(inflater, container, false)
        val registerFragmentFromDataCache = DataCache.registerFragment

        binding.apply {
            btnLogin.setOnClickListener {
                TermsOfServiceDialog(
                    DataCache.registerFragment!!,
                    DataCache.registerViewModel!!,
                    DataCache.registerBinding!!
                ).show(registerFragmentFromDataCache!!.parentFragmentManager, "Register_BottomDialog")
            }
        }

        return binding.root
    }
}