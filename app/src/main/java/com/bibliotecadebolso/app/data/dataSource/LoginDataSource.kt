package com.bibliotecadebolso.app.data.dataSource

import com.bibliotecadebolso.app.data.model.AuthTokens
import com.bibliotecadebolso.app.data.repository.BibliotecaDeBolsoRepository
import com.bibliotecadebolso.app.util.Result

class LoginDataSource {

    suspend fun login(email: String, password: String): Result<AuthTokens?> {
        val response = BibliotecaDeBolsoRepository.retrofit().login(email, password)

        return if (response.isSuccessful) {
            Result.Success(response.body())
        } else {
            Result.Error( response.code(), response.errorBody())
        }
    }

    suspend fun register(username: String, email: String, password: String): Result<String?> {
        val response = BibliotecaDeBolsoRepository.retrofit().register(email, username, password)

        return if (response.isSuccessful) {
            Result.Success(response.body())
        } else {
            Result.Error( response.code(), response.errorBody())
        }


    }

}