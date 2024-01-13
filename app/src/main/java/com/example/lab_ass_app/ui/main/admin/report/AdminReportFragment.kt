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
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
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
        initCurrentDate()
        initRefreshButton()
    }

    private fun initRefreshButton() {
        binding.btnListRefresh.setOnClickListener {
            initRV()
        }
    }

    private fun initCurrentDate() {
        //  Init date for TextView
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(Calendar.getInstance().time)
        val dateToBeDisplayed = "Updated as of $formattedDate"
        binding.tvCurrentDate.text = dateToBeDisplayed
    }

    private fun initRV() {
        adminReportViewModel.initRV(binding.rvReport, this@AdminReportFragment)
    }

    private fun initBackButton() {
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_adminReportFragment_to_homeAdminFragment)
        }
    }
}
