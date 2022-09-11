package com.bibliotecadebolso.app.data.model

data class UpdatedBook(
    val id: Long,
    val userId: Long,
    val title: String,
    val author: String,
    val isbn10: String,
    val isbn13: String,
    val publisher: String,
    val description: String,
    val thumbnail: String,
    val readStatus: ReadStatusEnum,
)
