package com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog.date_time

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.lab_ass_app.R
import com.example.lab_ass_app.databinding.FragmentSetDateDialogBinding
import com.example.lab_ass_app.utils.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class SetDateDialogFragment(
    private val dateTimeSelectedListener: DateTimeSelectedListener
) : DialogFragment() {
    private lateinit var binding: FragmentSetDateDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSetDateDialogBinding.inflate(inflater, container, false)

        initComponents()
        initClickableViews()

        return binding.root
    }

    private fun initComponents() {
        this.isCancelable = false
    }

    private fun initClickableViews() {
        val datePicker = binding.datePicker // Move the datePicker outside the lambda

        binding.apply {
            tvCancel.setOnClickListener {
                this@SetDateDialogFragment.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Canceled",
                    Toast.LENGTH_LONG
                ).show()
            }
            tvSet.setOnClickListener {
                val selectedDate = Calendar.getInstance()
                selectedDate.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)

                val currentDate = Calendar.getInstance()

                // Check if the selected date is less than or equal the current date or more than 6 days in the future
                if (selectedDate.before(currentDate) || selectedDate.timeInMillis == currentDate.timeInMillis || selectedDate.timeInMillis > currentDate.timeInMillis + 6 * 24 * 60 * 60 * 1000) {
                    Toast.makeText(
                        requireContext(),
                        "Please select a date within the allowed range (1-6 days)",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val day = datePicker.dayOfMonth
                    val month = datePicker.month + 1
                    val year = datePicker.year

                    Toast.makeText(
                        requireContext(),
                        "Date selected",
                        Toast.LENGTH_LONG
                    ).show()

                    dateTimeSelectedListener.onDateSelected("$day/$month/$year")
                    this@SetDateDialogFragment.dismiss()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.animation)
    }
}