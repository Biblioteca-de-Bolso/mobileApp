package com.bibliotecadebolso.app.data.model

data class Book(
    val bookId: Int,
    val userId: Int,
    val title: String,
    val author: String,
    val isbn10: String,
    val isbn13: String,
    val publisher: String,
    val description: String,
    val thumbnail: String,
    val readStatus: String,
    val borrowStatus: String,
    val createdAt: String,
    val updatedAt: String,
)
