package com.bibliotecadebolso.app.data.model.request

data class EditBorrow(
    val borrowId: Int,
    val borrowStatus: String,
    val contactName: String
)