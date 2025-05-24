package com.example.lab_ass_app.ui.main.opening

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentOpeningFifthPageBinding
import com.example.lab_ass_app.databinding.FragmentOpeningFourthPageBinding

class OpeningFifthPageFragment : Fragment() {
    private lateinit var binding: FragmentOpeningFifthPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOpeningFifthPageBinding.inflate(inflater, container, false)

        binding.apply {
            tvPrevious.setOnClickListener {
                findNavController().navigate(R.id.action_openingFifthPageFragment_to_openingFourthPageFragment)
            }
            tvNext.setOnClickListener {
                findNavController().navigate(R.id.action_openingFifthPageFragment_to_accountOpeningFragment)
            }
        }

        return binding.root
    }
}