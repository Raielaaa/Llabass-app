package com.example.lab_ass_app.ui.main.student_teacher.home.see_all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lab_ass_app.databinding.FragmentSeeAllDialogBinding
import com.example.lab_ass_app.ui.main.student_teacher.home.rv.HomeAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SeeAllDialog(
    private val adapter: SeeAllAdapter
) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentSeeAllDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeeAllDialogBinding.inflate(inflater, container, false)

        binding.rvSeeAll.adapter = adapter

        return binding.root
    }
}