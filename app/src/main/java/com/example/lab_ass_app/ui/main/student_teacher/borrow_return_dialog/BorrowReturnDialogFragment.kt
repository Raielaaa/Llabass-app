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
import androidx.lifecycle.ViewModelProvider
import com.example.lab_ass_app.MainActivity
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentBorrowReturnDialogBinding
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.date_time.DateTimeSelectedListener
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.date_time.SetDateDialogFragment
import com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.date_time.SetTimeDialogFragment
import com.example.lab_ass_app.utils.ItemInfoModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BorrowReturnDialogFragment(
    private val bitmap: Bitmap?,
    private val mainActivity: MainActivity,
    private val itemInfoModel: ItemInfoModel,
    private val currentUserLRN: String,
    private val currentUserEmail: String,
    private val currentUserType: String
) : BottomSheetDialogFragment(), DateTimeSelectedListener {
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
        initDisplayComponents()
        initSpinner()
        initReportTextView()
        initReportTv()
        binding.ivImage.setImageBitmap(bitmap)
        initDateTimeChooser()
        initProceedCancelButton()
    }

    private fun initProceedCancelButton() {
        binding.apply {
            btnCancel.setOnClickListener {
                this@BorrowReturnDialogFragment.dismiss()
            }
            btnProceed.setOnClickListener {
                borrowReturnDialogViewModel.insertBorrowInfoToFirebase()
            }
        }
    }

    private fun initDisplayComponents() {
        binding.apply {
            tvItemName.text = itemInfoModel.modelName
            tvName.text = currentUserEmail
            tvCategory.text = itemInfoModel.modelCategory
            tvStatus.text = itemInfoModel.modelStatus
            tvDescription.text = itemInfoModel.modelDescription
            tvLRN.text = "$currentUserLRN - $currentUserType"
        }
    }

    private fun initDateTimeChooser() {
        binding.apply {
            cvSetDate.setOnClickListener {
                SetDateDialogFragment(this@BorrowReturnDialogFragment).show(parentFragmentManager, "SetDate_Dialog")
            }
            cvSetTime.setOnClickListener {
                SetTimeDialogFragment(this@BorrowReturnDialogFragment).show(parentFragmentManager, "SetTime_Dialog")
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

    override fun onDateSelected(selectedDate: String) {
        binding.tvDate.text = selectedDate
    }

    override fun onTimeSelected(selectedTime: String) {
        binding.tvTime.text = selectedTime
    }
}