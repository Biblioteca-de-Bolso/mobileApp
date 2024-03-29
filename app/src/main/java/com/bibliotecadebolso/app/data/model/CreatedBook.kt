package com.bibliotecadebolso.app.data.model

data class CreatedBook(
    val id: Int,
    val userId: Int,
    val title: String,
    val author: String,
    val isbn10: String,
    val isbn13: String,
    val publisher: String,
    val description: String,
    val thumbnail: String,
    val readStatus: String,
    val borrowStatus: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val currentlyBorrowed: Boolean,
)
