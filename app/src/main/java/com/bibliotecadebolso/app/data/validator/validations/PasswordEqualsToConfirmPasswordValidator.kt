package com.bibliotecadebolso.app.data.validator.validations

import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.validator.IValidator
import com.bibliotecadebolso.app.data.validator.ValidationResult
import com.bibliotecadebolso.app.data.validator.ValidationResultUtils

class PasswordEqualsToConfirmPasswordValidator(
    val password: String,
    val confirmPassword: String,
): IValidator {
    override fun validate(): ValidationResult {
        if (password != confirmPassword) return ValidationResult(false, R.string.error_password_and_confirm_password_not_equals)
        return ValidationResultUtils.emptySuccess
    }
}