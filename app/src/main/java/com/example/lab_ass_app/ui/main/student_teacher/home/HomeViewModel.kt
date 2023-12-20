package com.example.lab_ass_app.ui.main.student_teacher.home

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.lab_ass_app.utils.Helper

class HomeViewModel : ViewModel() {
    private val TAG: String = "MyTag"

    fun takeQR(activity: Activity) {
        //  Method for starting the camera
        Helper.takeQR(activity)
    }

    //  Used for displaying toast messages
    private fun displayToastMessage(activity: Activity, message: String?) {
        Toast.makeText(
            activity,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}