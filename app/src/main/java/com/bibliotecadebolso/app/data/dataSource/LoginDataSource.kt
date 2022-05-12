package com.bibliotecadebolso.app.data.dataSource

import com.bibliotecadebolso.app.data.model.AuthTokens
import com.bibliotecadebolso.app.data.model.exceptions.NoInternetException
import com.bibliotecadebolso.app.data.model.response.APIResponse
import com.bibliotecadebolso.app.data.model.response.ErrorResponse
import com.bibliotecadebolso.app.data.model.response.UserObject
import com.bibliotecadebolso.app.data.repository.BibliotecaDeBolsoRepository
import com.bibliotecadebolso.app.util.RequestUtils
import com.bibliotecadebolso.app.util.Result
import okhttp3.ResponseBody
import retrofit2.Response
import java.lang.Exception
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class LoginDataSource {

    private val api = BibliotecaDeBolsoRepository.retrofit()
    suspend fun login(email: String, password: String ): Result<AuthTokens?> {

        var result: Result<AuthTokens> = RequestUtils.validateErrors {
            val response = api.login(email, password)
            val validationResponse = RequestUtils.isResponseSuccessful(response)

            if (validationResponse is Result.Success)
                Result.Success(validationResponse.response)
            else
                validationResponse as Result.Error
        }

        return result;
    }

    suspend fun register(username: String, email: String, password: String): Result<UserObject?> {

        var result: Result<UserObject> = RequestUtils.validateErrors {


            val response = api.register(email, username, password)
            val validationResponse = RequestUtils.isResponseSuccessful(response)

            if (validationResponse is Result.Success)
                Result.Success(validationResponse.response)
            else
                validationResponse as Result.Error

        }

        return result

    }

    private fun errorResponseTransformed(response: Response<*>): Result.Error {
        return Result.Error(response.code(), Result.transformToErrorResponse(response.errorBody()))
    }

}