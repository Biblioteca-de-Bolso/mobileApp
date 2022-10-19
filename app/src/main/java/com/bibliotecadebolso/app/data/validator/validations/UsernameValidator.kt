package com.bibliotecadebolso.app.data.validator.validations

import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.validator.IValidator
import com.bibliotecadebolso.app.data.validator.ValidationResult
import com.bibliotecadebolso.app.data.validator.ValidationResultUtils

class UsernameValidator(
    val username: String,
): IValidator{
    override fun validate(): ValidationResult {
        val isUsernameValid = username.isNotEmpty() && username.length in 3..64
        if (!isUsernameValid) return ValidationResult(false, R.string.invalid_username)
        return ValidationResultUtils.emptySuccess
    }
}