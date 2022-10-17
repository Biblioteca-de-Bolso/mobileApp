package com.bibliotecadebolso.app.data.validator

import android.content.Context
import com.bibliotecadebolso.app.R
import com.google.android.material.textfield.TextInputLayout

object ValidationResultUtils {
    val emptySuccess = ValidationResult(true, R.string.empty_string)

    fun showOnTextInputLayoutAndResultIfHasError(context: Context, map: Map<TextInputLayout, IValidator>): Boolean {
        var hasError = false
        map.forEach{
            val validationResult = it.value.validate()
            it.key.error = context.getString(validationResult.message)
            if (!validationResult.isSuccess) hasError = true
        }
        return hasError
    }
}