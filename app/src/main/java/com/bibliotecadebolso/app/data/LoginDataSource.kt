package com.bibliotecadebolso.app.data

import android.util.Log
import com.bibliotecadebolso.app.data.LoginRepository
import com.bibliotecadebolso.app.data.Result
import com.bibliotecadebolso.app.data.model.AuthTokens

class LoginDataSource {

    suspend fun login(email: String, password: String): Result<AuthTokens?> {
        val response = LoginRepository.retrofit().login(email, password)

        return if (response.isSuccessful) {
            Result.Success(response.body())
        } else {
            Result.Error( response.code(), response.errorBody())
        }
    }

    suspend fun register(username: String, email: String, password: String): Result<String?> {
        val response = LoginRepository.retrofit().signUp(email, username, password)

        return if (response.isSuccessful) {
            Result.Success(response.body())
        } else {
            Result.Error( response.code(), response.errorBody())
        }


    }

}