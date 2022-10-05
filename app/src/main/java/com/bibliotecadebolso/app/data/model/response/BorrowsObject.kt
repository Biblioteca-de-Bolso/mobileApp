package com.bibliotecadebolso.app.data.model.response

import com.bibliotecadebolso.app.data.model.request.Borrow

data class BorrowsObject(
    val borrows: List<Borrow>
)