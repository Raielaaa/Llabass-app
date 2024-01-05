package com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentReturnItemBinding

class ReturnItemFragment : Fragment() {
    private lateinit var binding: FragmentReturnItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentReturnItemBinding.inflate(inflater, container, false)
        return binding.root
    }
}