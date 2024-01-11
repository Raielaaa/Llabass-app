package com.example.lab_ass_app.ui.main.admin.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentAdminProfileBinding

class AdminProfileFragment : Fragment() {
    private lateinit var binding: FragmentAdminProfileBinding
    private lateinit var mViewModel: AdminProfileViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminProfileBinding.inflate(inflater, container, false)
        mViewModel = ViewModelProvider(this)[AdminProfileViewModel::class.java]

        return binding.root
    }
}
