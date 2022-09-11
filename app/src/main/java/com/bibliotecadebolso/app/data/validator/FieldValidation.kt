package com.bibliotecadebolso.app.data.validator

interface FieldValidation {
    fun validate(): ValidationError?
}