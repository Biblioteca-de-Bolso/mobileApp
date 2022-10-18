package com.bibliotecadebolso.app.data.validator.validations

import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.validator.IValidator
import com.bibliotecadebolso.app.data.validator.ValidationResult
import com.bibliotecadebolso.app.data.validator.ValidationResultUtils

class ContactNameValidator(
    val contactName: String
) : IValidator {
    override fun validate(): ValidationResult {
        val isValid = contactName.isNotEmpty() && contactName.length in 1..64
        if (!isValid) return ValidationResult(
            false,
            R.string.error_borrow_must_have_between_1_and_64
        )

        return ValidationResultUtils.emptySuccess
    }
}