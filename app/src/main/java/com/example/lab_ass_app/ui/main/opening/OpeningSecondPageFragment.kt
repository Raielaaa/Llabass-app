package com.example.lab_ass_app.ui.main.opening

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentOpeningFirstPageBinding
import com.example.lab_ass_app.databinding.FragmentOpeningSecondPageBinding

class OpeningSecondPageFragment : Fragment() {
    private lateinit var binding: FragmentOpeningSecondPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOpeningSecondPageBinding.inflate(inflater, container, false)

        binding.apply {
            tvPrevious.setOnClickListener {
                findNavController().navigate(R.id.action_openingSecondPageFragment_to_openingFirstPageFragment)
            }
            tvNext.setOnClickListener {
                findNavController().navigate(R.id.action_openingSecondPageFragment_to_openingThirdPageFragment)
            }
        }

        return binding.root
    }
}