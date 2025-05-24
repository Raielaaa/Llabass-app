package com.example.lab_ass_app.ui.main.opening

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentOpeningFourthPageBinding
import com.example.lab_ass_app.databinding.FragmentOpeningThirdPageBinding

class OpeningFourthPageFragment : Fragment() {
    private lateinit var binding: FragmentOpeningFourthPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOpeningFourthPageBinding.inflate(inflater, container, false)

        binding.apply {
            tvPrevious.setOnClickListener {
                findNavController().navigate(R.id.action_openingFourthPageFragment_to_openingThirdPageFragment)
            }
            tvNext.setOnClickListener {
                findNavController().navigate(R.id.action_openingFourthPageFragment_to_openingFifthPageFragment)
            }
        }

        return binding.root
    }
}