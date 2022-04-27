package com.bibliotecadebolso.app.data.model.response

data class APIResponse<T>(
    val status: String?,
    val response: T,
    val code: String?,
    val message: String?
)
