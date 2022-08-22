package com.bibliotecadebolso.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val isbn10: String,
    val isbn13: String,
    val author: String,
    val description: String,
    val publisher: String,
    val subtitle: String,
    val thumbnail: String,
    val title: String,
    val readStatusEnum: ReadStatusEnum? = null,
) : Parcelable