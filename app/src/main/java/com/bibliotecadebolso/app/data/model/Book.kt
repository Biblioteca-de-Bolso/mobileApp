package com.bibliotecadebolso.app.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val ISBN_10: String,
    val ISBN_13: String,
    val author: String,
    val description: String,
    val publisher: String,
    val subtitle: String,
    val thumbnail: String,
    val title: String,
    val readStatusEnum: ReadStatusEnum? = null,
) : Parcelable