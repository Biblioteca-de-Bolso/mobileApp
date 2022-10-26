package com.bibliotecadebolso.app.data.model.response

import java.time.LocalDateTime

data class ProfileResponse(
    val id: Long,
    val name: String,
    val email: String,
    val active: Boolean,
    val activationCode: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
