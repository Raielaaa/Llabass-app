package com.example.lab_ass_app.ui.main.opening

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentOpeningSecondPageBinding
import com.example.lab_ass_app.databinding.FragmentOpeningThirdPageBinding

class OpeningThirdPageFragment : Fragment() {
    private lateinit var binding: FragmentOpeningThirdPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOpeningThirdPageBinding.inflate(inflater, container, false)

        binding.apply {
            tvPrevious.setOnClickListener {
                findNavController().navigate(R.id.action_openingThirdPageFragment_to_openingSecondPageFragment)
            }
            tvNext.setOnClickListener {
                findNavController().navigate(R.id.action_openingThirdPageFragment_to_openingFourthPageFragment)
            }
        }

        return binding.root
    }
}