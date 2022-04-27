package com.bibliotecadebolso.app.data.dataSource

import com.bibliotecadebolso.app.data.model.AuthTokens
import com.bibliotecadebolso.app.data.model.response.APIResponse
import com.bibliotecadebolso.app.data.model.response.ErrorResponse
import com.bibliotecadebolso.app.data.repository.BibliotecaDeBolsoRepository
import com.bibliotecadebolso.app.util.Result
import okhttp3.ResponseBody
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class LoginDataSource {

    suspend fun login(email: String, password: String): Result<AuthTokens?> {
        val response = BibliotecaDeBolsoRepository.retrofit().login(email, password)
        var result: Result<AuthTokens>

        try {
            if (response.isSuccessful) {
                if (response.body()?.status.equals("ok"))
                    result = Result.Success(response.body()!!.response)
                else
                    result = errorResponseTransformed(response)
            } else {
                result = errorResponseTransformed(response)
            }
        } catch (e: UnknownHostException) {
            result = Result.Error(
                null,
                ErrorResponse("error", "unknownHost", "sem conexão com a internet")
            )
        }

        return result;
    }

    suspend fun register(username: String, email: String, password: String): Result<String?> {
        val response = BibliotecaDeBolsoRepository.retrofit().register(email, username, password)

        return if (response.isSuccessful) {
            Result.Success(response.body())
        } else {
            errorResponseTransformed(response)
        }


    }

    private fun errorResponseTransformed(response: Response<*>): Result.Error {
        return Result.Error(response.code(), Result.transformToErrorResponse(response.errorBody()))
    }

}