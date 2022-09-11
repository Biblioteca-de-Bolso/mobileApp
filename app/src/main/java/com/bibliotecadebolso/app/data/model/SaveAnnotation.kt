package com.bibliotecadebolso.app.data.model

data class SaveAnnotation(
    val bookId: Int,
    val title: String,
    val text: String,
    val reference: String = ""
)
