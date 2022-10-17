package com.bibliotecadebolso.app.data.validator.validations

import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.validator.IValidator
import com.bibliotecadebolso.app.data.validator.ValidationResult
import com.bibliotecadebolso.app.data.validator.ValidationResultUtils

class PasswordValidator(
    val password: String,
): IValidator {
    override fun validate(): ValidationResult {
        val isPasswordValid = password.isNotEmpty() && (password.length in 8..32)
        if (!isPasswordValid) return ValidationResult(false, R.string.error_password_length_between_8_and_32)
        return ValidationResultUtils.emptySuccess
    }

}