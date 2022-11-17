package com.bibliotecadebolso.app.data.model

import com.bibliotecadebolso.app.data.model.request.BookTitle

data class Annotation(
    val id: Int,
    val userId: Int,
    val bookId: Int,
    val title: String,
    val text: String,
    val reference: String,
    val createdAt: String,
    val updatedAt: String,
    val book: BookTitle?
)
