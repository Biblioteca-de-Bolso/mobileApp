package com.bibliotecadebolso.app.data.model.response

data class BookResponse(
    val title: String,
    val author: String = "",
    val isbn: String = "",
    val publisher: String = "",
    val description: String = "",
    val thumbnail: String = ""
)