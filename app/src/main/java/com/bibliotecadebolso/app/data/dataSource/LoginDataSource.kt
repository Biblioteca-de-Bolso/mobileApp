package com.bibliotecadebolso.app.data.dataSource

import com.bibliotecadebolso.app.data.model.AuthTokens
import com.bibliotecadebolso.app.data.model.DeleteForm
import com.bibliotecadebolso.app.data.model.RefreshTokenObject
import com.bibliotecadebolso.app.data.model.response.DeleteAccountResponse
import com.bibliotecadebolso.app.data.model.response.UserObject
import com.bibliotecadebolso.app.data.repository.BibliotecaDeBolsoRepository
import com.bibliotecadebolso.app.ui.user.requestChangePassword.ChangePasswordForm
import com.bibliotecadebolso.app.ui.user.requestChangePassword.form.RequestChangePasswordForm
import com.bibliotecadebolso.app.util.RequestUtils
import com.bibliotecadebolso.app.util.Result
import retrofit2.Response

class LoginDataSource {

    private val api = BibliotecaDeBolsoRepository.retrofit()
    suspend fun login(email: String, password: String): Result<AuthTokens?> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.login(email, password)
            RequestUtils.returnResponseTransformedIntoResult(response)
        }
    }

    suspend fun register(username: String, email: String, password: String): Result<UserObject?> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.register(email, username, password)
            RequestUtils.returnResponseTransformedIntoResult(response)
        }
    }

    suspend fun getNewAccessToken(accessToken: String, refreshToken: String): Result<AuthTokens?> {
        val refreshTokenObject = RefreshTokenObject(refreshToken)
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response =
                api.getNewAccessToken("Bearer $accessToken", refreshTokenObject)
            RequestUtils.returnResponseTransformedIntoResult(response)
        }
    }

    suspend fun deleteAccount(
        accessTokens: String,
        deleteForm: DeleteForm
    ): Result<DeleteAccountResponse> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.delete("Bearer $accessTokens", deleteForm)
            RequestUtils.returnResponseTransformedIntoResult(response)
        }
    }

    suspend fun requestChangePassword(
        requestChangePasswordForm: RequestChangePasswordForm
    ): Result<Boolean> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.requestChangePassword(requestChangePasswordForm)
            RequestUtils.returnResponseTransformedIntoResult(response)
        }
    }

    suspend fun changePassword(changePasswordForm: ChangePasswordForm): Result<Boolean> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.changePassword(changePasswordForm)
            RequestUtils.returnResponseTransformedIntoResult(response)
        }

    }

    private fun errorResponseTransformed(response: Response<*>): Result.Error {
        return Result.Error(response.code(), Result.transformToErrorResponse(response.errorBody()))
    }

}