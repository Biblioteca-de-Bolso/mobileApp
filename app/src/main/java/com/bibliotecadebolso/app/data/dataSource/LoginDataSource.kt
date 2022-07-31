package com.bibliotecadebolso.app.data.dataSource

import com.bibliotecadebolso.app.data.model.AuthTokens
import com.bibliotecadebolso.app.data.model.DeleteForm
import com.bibliotecadebolso.app.data.model.response.DeleteAccountResponse
import com.bibliotecadebolso.app.data.model.response.UserObject
import com.bibliotecadebolso.app.data.repository.BibliotecaDeBolsoRepository
import com.bibliotecadebolso.app.util.RequestUtils
import com.bibliotecadebolso.app.util.Result
import retrofit2.Response

class LoginDataSource {

    private val api = BibliotecaDeBolsoRepository.retrofit()
    suspend fun login(email: String, password: String): Result<AuthTokens?> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.login(email, password)
            val validationResponse = RequestUtils.isResponseSuccessful(response)

            if (validationResponse is Result.Success) Result.Success(validationResponse.response)
            else validationResponse as Result.Error
        }
    }

    suspend fun register(username: String, email: String, password: String): Result<UserObject?> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.register(email, username, password)
            val responseResult = RequestUtils.isResponseSuccessful(response)

            if (responseResult is Result.Success) Result.Success(responseResult.response)
            else responseResult as Result.Error
        }
    }

    suspend fun getNewAccessToken(refreshToken: String): Result<AuthTokens?> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.getNewAccessToken(refreshToken)
            val responseResult = RequestUtils.isResponseSuccessful(response)

            if (responseResult is Result.Success) Result.Success(responseResult.response)
            else responseResult as Result.Error
        }
    }

    suspend fun deleteAccount(
        accessTokens: String,
        deleteForm: DeleteForm
    ): Result<DeleteAccountResponse> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.delete("Bearer $accessTokens", deleteForm)
            val validationResponse = RequestUtils.isResponseSuccessful(response)

            if (validationResponse is Result.Success) Result.Success(validationResponse.response)
            else validationResponse as Result.Error
        }
    }

    private fun errorResponseTransformed(response: Response<*>): Result.Error {
        return Result.Error(response.code(), Result.transformToErrorResponse(response.errorBody()))
    }

}