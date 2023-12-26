package com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.lab_ass_app.R
import com.example.lab_ass_app.utils.Helper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BorrowReturnDialogViewModel : ViewModel() {
    private val calendar = Calendar.getInstance()

    fun showDatePicker(
        context: Activity,
        tvDate: TextView
    ) {
        Helper.displayCustomDialog(
            context,
            R.layout.fragment_set_date_dialog
        )
    }
}
