package com.bibliotecadebolso.app.data.dataSource

import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.response.BookResponse
import com.bibliotecadebolso.app.data.model.response.ErrorResponse
import com.bibliotecadebolso.app.data.repository.BibliotecaDeBolsoRepository
import com.bibliotecadebolso.app.util.RequestUtils
import com.bibliotecadebolso.app.util.Result
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
    ): Result<Book> {
        val bookResponse = BookResponse(title, author, isbn, publisher, description, thumbnail)
        val response =
            api.createBook("Bearer $accessToken", bookResponse)
        var result: Result<Book>

        try {
            result = RequestUtils.isResponseSuccessful(response)
        } catch (e: UnknownHostException) {
            result = Result.Error(
                null,
                ErrorResponse("error", "unknownHost", "sem conexão com a internet")
            )
        }

        return result;
    }

    suspend fun list(
        accessToken: String,
        pageNum: Int,
    ): Result<List<Book>> {
        val response = api.bookList("Bearer $accessToken", pageNum)
        var result: Result<List<Book>>

        try {
            val resultNotFormatted = RequestUtils.isResponseSuccessful(response)

            if (resultNotFormatted is Result.Success)
                result = Result.Success(resultNotFormatted.response.books)
            else
                result = resultNotFormatted as Result.Error
        } catch (e: UnknownHostException) {
            result = Result.Error(
                null,
                ErrorResponse("error", "unknownHost", "sem conexão com a internet")
            )
        }

        return result;
    }
}