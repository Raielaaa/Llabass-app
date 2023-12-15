package com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentReportBinding

class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_reportFragment_to_homeFragment)
        }

        return binding.root
    }
}