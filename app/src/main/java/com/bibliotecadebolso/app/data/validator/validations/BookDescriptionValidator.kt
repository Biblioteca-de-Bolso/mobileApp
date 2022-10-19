package com.bibliotecadebolso.app.data.validator.validations

import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.validator.IValidator
import com.bibliotecadebolso.app.data.validator.ValidationResult
import com.bibliotecadebolso.app.data.validator.ValidationResultUtils

class BookDescriptionValidator(
    val description: String,
): IValidator {
    override fun validate(): ValidationResult {
        val isTitleValid = description.length in 0..5000
        if (!isTitleValid) return ValidationResult(false, R.string.error_must_be_between_0_5000)

        return ValidationResultUtils.emptySuccess
    }
}