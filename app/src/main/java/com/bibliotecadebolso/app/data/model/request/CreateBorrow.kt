package com.bibliotecadebolso.app.data.model.request

data class CreateBorrow(
    val bookId: Int,
    val contactName: String
)