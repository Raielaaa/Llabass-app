package com.example.lab_ass_app.ui.main.student_teacher.borrow_return_dialog

data class BorrowModel(
    val modelEmail: String,
    val modelLRN: String,
    val modelUserType: String,
    val modelUserID: String,
    val modelItemCode: String,
    val modelItemName: String,
    val modelItemCategory: String,
    val modelItemSize: String,
    val modelBorrowDateTime: String,
    val modelBorrowDeadlineDateTime: String
)