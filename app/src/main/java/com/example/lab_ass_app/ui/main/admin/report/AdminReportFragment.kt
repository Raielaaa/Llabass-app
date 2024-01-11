package com.example.lab_ass_app.ui.main.admin.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentAdminReportBinding

class AdminReportFragment : Fragment() {
    private lateinit var binding: FragmentAdminReportBinding
    private lateinit var mViewModel: AdminReportViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminReportBinding.inflate(inflater, container, false)
        mViewModel = ViewModelProvider(this)[AdminReportViewModel::class.java]

        return binding.root
    }
}
