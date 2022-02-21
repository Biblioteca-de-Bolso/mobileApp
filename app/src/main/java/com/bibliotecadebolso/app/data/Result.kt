package com.bibliotecadebolso.app.data

import okhttp3.ResponseBody

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out T> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(
        val errorCode: Int?,
        val errorBody: ResponseBody?
    ) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "${errorCode}, ${errorBody.toString()}"
        }
    }
}