package com.bibliotecadebolso.app.data.model

data class UpdateAnnotation(
    val annotationId: Int,
    val title: String,
    val text: String,
    val reference: String
)
