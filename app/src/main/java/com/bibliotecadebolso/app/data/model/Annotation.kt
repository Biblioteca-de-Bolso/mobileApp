package com.bibliotecadebolso.app.data.model

data class Annotation(
    val id: Int,
    val userId: Int,
    val bookId: Int,
    val title: String,
    val text: String,
    val reference: String,
    val createdAt: String,
    val updatedAt: String,
)
