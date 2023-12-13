package com.example.lab_ass_app.ui.main.student_teacher.home

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.lab_ass_app.ui.Helper

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