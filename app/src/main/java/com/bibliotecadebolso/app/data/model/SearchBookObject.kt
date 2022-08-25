package com.bibliotecadebolso.app.data.model

import com.bibliotecadebolso.app.data.model.search.BookSearch

data class SearchBookObject(
    val books: List<BookSearch>
)