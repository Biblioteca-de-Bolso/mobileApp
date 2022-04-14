package com.bibliotecadebolso.app.util

import com.bibliotecadebolso.app.data.model.response.ErrorResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody

sealed class Result<out T> {

    data class Success<out T>(val response: T) : Result<T>()
    data class Error(
        val errorCode: Int?,
        val errorBody: ErrorResponse
    ) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$response]"
            is Error -> "${errorCode}, $errorBody"
        }
    }

    companion object {
        fun transformToErrorResponse(responseBody: ResponseBody?): ErrorResponse {
            val type = object : TypeToken<ErrorResponse>() {}.type
            return Gson().fromJson(responseBody?.charStream(), type)
        }
    }

}