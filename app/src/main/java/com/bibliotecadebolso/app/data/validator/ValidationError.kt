package com.bibliotecadebolso.app.data.validator

data class ValidationError(
    val field: String,
    val errorMessage: String,
)