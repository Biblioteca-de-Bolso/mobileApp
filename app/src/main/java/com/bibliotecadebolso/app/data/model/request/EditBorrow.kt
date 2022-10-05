package com.bibliotecadebolso.app.data.model.request

data class EditBorrow(
    val borrowId: Int,
    val borrowStatus: BorrowStatus,
    val contactName: String
)