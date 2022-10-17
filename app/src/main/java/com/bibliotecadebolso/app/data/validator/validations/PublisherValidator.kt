package com.bibliotecadebolso.app.data.validator.validations

import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.validator.IValidator
import com.bibliotecadebolso.app.data.validator.ValidationResult
import com.bibliotecadebolso.app.data.validator.ValidationResultUtils

class PublisherValidator(val publisher: String): IValidator {
    override fun validate(): ValidationResult {
        val isPublisherValid = publisher.length in 0..128
        if (!isPublisherValid) return ValidationResult(false, R.string.error_must_be_between_0_128)

        return ValidationResultUtils.emptySuccess
    }
}