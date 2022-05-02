package com.bibliotecadebolso.app.data.dataSource

import com.bibliotecadebolso.app.data.model.AuthTokens
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.response.BookResponse
import com.bibliotecadebolso.app.data.model.response.ErrorResponse
import com.bibliotecadebolso.app.data.repository.BibliotecaDeBolsoRepository
import com.bibliotecadebolso.app.util.Result
import retrofit2.Response
import retrofit2.http.Field
import java.net.UnknownHostException

object BookDataSource {

    private val api = BibliotecaDeBolsoRepository.retrofit()

    suspend fun create(
        accessToken: String,
        title: String,
        author: String = "",
        isbn: String = "",
        publisher: String = "",
        description: String = "",
        thumbnail: String = ""
    ): Result<Boolean> {
        val bookResponse = BookResponse(title, author, isbn, publisher, description, thumbnail)
        val response =
            api.createBook("Bearer $accessToken", bookResponse)
        var result: Result<Boolean>

        try {
            if (response.isSuccessful) {
                if (response.body()?.status.equals("ok"))
                    result = Result.Success(true)
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

    private fun errorResponseTransformed(response: Response<*>): Result.Error {
        return Result.Error(response.code(), Result.transformToErrorResponse(response.errorBody()))
    }
}