package com.example.lab_ass_app.ui.main.student_teacher.experiments.homeList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentExperimentsBinding

class ExperimentsFragment : Fragment() {
    private lateinit var viewModel: ExperimentsViewModel
    private lateinit var binding: FragmentExperimentsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExperimentsBinding.inflate(inflater, container, false)

        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_experimentsFragment_to_homeFragment)
        }

        return binding.root
    }
}