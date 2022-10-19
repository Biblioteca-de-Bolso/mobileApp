package com.bibliotecadebolso.app.data.validator.validations

import android.text.TextUtils
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.validator.*

class EmailValidation(
    val email: String
) : IValidator {
    override fun validate(): ValidationResult {
        if (
            TextUtils.isEmpty(email) ||
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        ) return ValidationResult(false, R.string.error_email_not_valid)

        return ValidationResultUtils.emptySuccess
    }

}