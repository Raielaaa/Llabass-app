package com.example.lab_ass_app.ui.main.opening

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentOpeningFirstPageBinding

class OpeningFirstPageFragment : Fragment() {
    private lateinit var binding: FragmentOpeningFirstPageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOpeningFirstPageBinding.inflate(inflater, container, false)

        binding.apply {
            tvNext.setOnClickListener {
                findNavController().navigate(R.id.action_openingFirstPageFragment_to_openingSecondPageFragment)
            }
        }

        return binding.root
    }
}