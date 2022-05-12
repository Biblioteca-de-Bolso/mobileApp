package com.bibliotecadebolso.app.data.model

data class User(
    val activationCode: String,
    val active: Boolean,
    val createdAt: String,
    val email: String,
    val id: Int,
    val name: String,
    val password: String,
    val updatedAt: String
)