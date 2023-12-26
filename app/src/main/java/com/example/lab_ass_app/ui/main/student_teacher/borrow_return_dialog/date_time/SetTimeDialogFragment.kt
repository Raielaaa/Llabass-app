package com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.date_time

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentSetTimeDialogBinding
import com.example.lab_ass_app.utils.Constants
import java.text.DecimalFormat

class SetTimeDialogFragment(
    private val dateTimeSelectedListener: DateTimeSelectedListener
) : DialogFragment() {
    private lateinit var binding: FragmentSetTimeDialogBinding
    private val dateTimeDecimalFormal: DecimalFormat by lazy { DecimalFormat("00") }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSetTimeDialogBinding.inflate(inflater, container, false)

        initComponent()
        initClickableViews()

        return binding.root
    }

    private fun initComponent() {
        this.isCancelable = false
        binding.timePicker.setIs24HourView(false)
    }

    private fun initClickableViews() {
        binding.apply {
            tvCancel.setOnClickListener {
                this@SetTimeDialogFragment.dismiss()
            }
            tvSet.setOnClickListener {
                val selectedHour: Int = if (timePicker.hour > 12) timePicker.hour - 12 else timePicker.hour
                val selectedMinute: Int = timePicker.minute
                val amOrPm = if (timePicker.hour >= 12) "PM" else "AM"

                Toast.makeText(
                    requireContext(),
                    "Time selected",
                    Toast.LENGTH_LONG
                ).show()

                dateTimeSelectedListener.onTimeSelected("${dateTimeDecimalFormal.format(selectedHour)} : ${dateTimeDecimalFormal.format(selectedMinute)} $amOrPm")
                this@SetTimeDialogFragment.dismiss()
            }
        }
    }
}