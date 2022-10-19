package com.bibliotecadebolso.app.data.validator

import com.bibliotecadebolso.app.R

abstract class BaseValidator: IValidator {
    companion object {
        fun validate(vararg validators: IValidator): ValidationResult {
            validators.forEach {
                val result = it.validate()
                if (!result.isSuccess) return result
            }
            return ValidationResult(true, R.string.label_empty)
        }
    }
}