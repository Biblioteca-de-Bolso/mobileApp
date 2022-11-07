package com.bibliotecadebolso.app.data.model.request

import java.time.LocalDateTime

data class Borrow(
    val id: Int,
    val bookId: Int,
    val userId: Int,
    val contactName: String,
    val borrowStatus: BorrowStatus,
    val borrowDate: LocalDateTime,
    val devolutionDate: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val book: BookTitle,
)

