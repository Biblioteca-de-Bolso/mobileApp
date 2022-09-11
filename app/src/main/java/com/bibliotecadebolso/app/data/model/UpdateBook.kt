package com.bibliotecadebolso.app.data.model

data class UpdateBook(
    val bookId: Long,
    val title: String? = null,
    val author: String? = null,
    val isbn: String? = null,
    val publisher: String? = null,
    val description: String? = null,
    val readStatus: ReadStatusEnum? = null,
)
