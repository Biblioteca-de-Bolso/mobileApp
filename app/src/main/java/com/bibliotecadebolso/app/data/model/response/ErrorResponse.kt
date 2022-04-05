package com.bibliotecadebolso.app.data.model.response

data class ErrorResponse(
    val status: String,
    val code: String,
    val message: String
)
