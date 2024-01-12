package com.example.lab_ass_app.ui.main.admin.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentAdminReportBinding

class AdminReportFragment : Fragment() {
    private lateinit var binding: FragmentAdminReportBinding
    private lateinit var adminReportViewModel: AdminReportViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminReportBinding.inflate(inflater, container, false)
        adminReportViewModel = ViewModelProvider(this)[AdminReportViewModel::class.java]

        initComponents()

        return binding.root
    }

    private fun initComponents() {
        initBackButton()
        initRV()
    }

    private fun initRV() {
        adminReportViewModel.initRV()
    }

    private fun initBackButton() {
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_adminReportFragment_to_homeAdminFragment)
        }
    }
}
