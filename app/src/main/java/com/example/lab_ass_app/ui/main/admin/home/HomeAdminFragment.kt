package com.example.lab_ass_app.ui.main.admin.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentHomeAdminBinding

class HomeAdminFragment : Fragment() {
    private lateinit var binding: FragmentHomeAdminBinding
    private lateinit var mViewModel: HomeAdminViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        mViewModel = ViewModelProvider(this)[HomeAdminViewModel::class.java]

        return binding.root
    }
}
