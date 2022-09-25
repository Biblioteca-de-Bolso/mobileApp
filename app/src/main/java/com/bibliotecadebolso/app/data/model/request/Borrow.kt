package com.bibliotecadebolso.app.data.model.request

import java.time.LocalDateTime

data class Borrow(
    val id: Int,
    val userId: Int,
    val contactName: String,
    val BorrowStatus: BorrowStatus,
    val borrowDate: LocalDateTime,
    val devolutionDate: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val book: BookTitle
)

data class BookTitle(
    val title: String
)
