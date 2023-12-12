package com.example.lab_ass_app.ui.main.student_teacher.profile.rv

import android.net.Uri

data class ProfileModel(
    val imageModel: Uri,
    val itemModel: String,
    val statusModel: String,
    val borrowDateModel: String,
    val borrowDeadlineModel: String
)
