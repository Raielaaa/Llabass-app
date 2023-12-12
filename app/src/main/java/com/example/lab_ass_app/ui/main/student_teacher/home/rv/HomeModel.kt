package com.example.lab_ass_app.ui.main.student_teacher.home.rv

import android.net.Uri

data class HomeModel (
    val imageModel: Uri,
    val itemNameModel: String,
    val itemCodeModel: String,
    val itemBorrowCountModel: String,
    val itemStatusModel: String
)