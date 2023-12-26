package com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog

import android.graphics.Bitmap
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.navigation.fragment.findNavController
import android.app.DatePickerDialog
import androidx.lifecycle.ViewModelProvider
import com.example.lab_ass_app.MainActivity
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentBorrowReturnDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BorrowReturnDialogFragment(
    private val bitmap: Bitmap?,
    private val mainActivity: MainActivity
) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBorrowReturnDialogBinding
    private lateinit var borrowReturnDialogViewModel: BorrowReturnDialogViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBorrowReturnDialogBinding.inflate(inflater, container, false)
        borrowReturnDialogViewModel = ViewModelProvider(this@BorrowReturnDialogFragment)[BorrowReturnDialogViewModel::class.java]

        initViews()

        return binding.root
    }

    private fun initViews() {
        initSpinner()
        initReportTextView()
        initReportTv()
        binding.ivImage.setImageBitmap(bitmap)
        initDateTimeChooser()
    }

    private fun initDateTimeChooser() {
        binding.apply {
            cvSetDate.setOnClickListener {
                borrowReturnDialogViewModel.showDatePicker(
                    requireActivity(),
                    tvDate
                )
            }
            cvSetTime.setOnClickListener {

            }
        }
    }

    private fun initReportTv() {
        binding.tvReport.setOnClickListener {
            Toast.makeText(mainActivity, "clicked", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.reportFragment)
            this.dismiss()
        }
    }

    private fun initReportTextView() {
        binding.tvReport.text = SpannableStringBuilder()
            .append("Encountered a problem? ")
            .color(ContextCompat.getColor(requireContext(), R.color.Theme_color_main)) { append("REPORT NOW!") }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }

    private fun initSpinner() {
        binding.apply {
            val spinner: Spinner = spUser2
            val spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.borrow_return_option,
                android.R.layout.simple_spinner_dropdown_item
            )

            spinner.adapter = spinnerAdapter
        }
    }
}