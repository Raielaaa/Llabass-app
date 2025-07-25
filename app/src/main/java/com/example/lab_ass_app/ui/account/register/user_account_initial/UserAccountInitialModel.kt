package com.example.lab_ass_app.ui.account.register.user_account_initial

data class UserAccountInitialModel(
    val userFirstName: String,
    val userLastName: String,
    val userIDModel: String,
    val userLRNModel: String,
    val userEmailModel: String,
    val userTypeModel: String,
    val verifiedPhoneNumber: String? = null
)