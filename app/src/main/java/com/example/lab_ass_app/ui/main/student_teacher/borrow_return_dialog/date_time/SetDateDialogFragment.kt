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
                Log.d(Constants.TAG, "initClickableViews: cancel")
                Toast.makeText(
                    requireContext(),
                    "cancel",
                    Toast.LENGTH_LONG
                ).show()
            }
            tvSet.setOnClickListener {
                val day = datePicker.dayOfMonth
                val month = datePicker.month + 1
                val year = datePicker.year

                Toast.makeText(
                    requireContext(),
                    "Date selected",
                    Toast.LENGTH_LONG
                ).show()
                dateTimeSelectedListener.onDateSelected("$day · $month · $year")
                this@SetDateDialogFragment.dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setWindowAnimations(R.style.animation)
    }

    interface DateSelectedListener {
        fun onDateSelected(selectedDate: String)
    }
}