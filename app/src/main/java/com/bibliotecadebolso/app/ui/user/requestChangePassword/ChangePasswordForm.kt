package com.bibliotecadebolso.app.ui.user.requestChangePassword

data class ChangePasswordForm(
    val email: String,
    val recoverCode: String,
    val newPassword: String,
)