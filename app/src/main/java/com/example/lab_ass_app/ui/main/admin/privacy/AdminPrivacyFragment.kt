package com.example.lab_ass_app.ui.main.admin.privacy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentAdminPrivacyBinding

class AdminPrivacyFragment : Fragment() {
    private lateinit var binding: FragmentAdminPrivacyBinding
    private lateinit var mViewModel: AdminPrivacyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminPrivacyBinding.inflate(inflater, container, false)
        mViewModel = ViewModelProvider(this)[AdminPrivacyViewModel::class.java]

        return binding.root
    }
}
