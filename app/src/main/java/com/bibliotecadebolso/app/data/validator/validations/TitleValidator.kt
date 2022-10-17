package com.bibliotecadebolso.app.data.validator.validations

import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.validator.IValidator
import com.bibliotecadebolso.app.data.validator.ValidationResult
import com.bibliotecadebolso.app.data.validator.ValidationResultUtils

class TitleValidator(
    val title: String,
) : IValidator {
    override fun validate(): ValidationResult {
        val isTitleValid = title.isNotEmpty() && title.length in 1..128
        if (!isTitleValid) return ValidationResult(false, R.string.error_must_be_beetween_1_128)

        return ValidationResultUtils.emptySuccess
    }
}