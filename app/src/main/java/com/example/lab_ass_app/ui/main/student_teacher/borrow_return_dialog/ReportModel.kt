package com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog

data class ReportModel(
    val modelSenderEmail: String,
    val modelSenderID: String,
    val modelSenderLRN: String,
    val modelSenderUserType: String,
    val modelItemName: String,
    val modelItemCategory: String,
    val modelItemCode: String,
    val modelItemSize: String,
    val modelReportTitle: String,
    val modelReportContent: String,
    val modelCurrentDateTime: String
)
