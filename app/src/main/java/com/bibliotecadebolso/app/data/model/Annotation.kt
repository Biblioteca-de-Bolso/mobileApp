package com.bibliotecadebolso.app.data.model

data class Annotation(
    val bookId: Int,
    val title: String,
    val text: String,
    val page: Int = 0
)
