package com.bibliotecadebolso.app.data.dataSource

import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.CreatedBook
import com.bibliotecadebolso.app.data.model.exceptions.NoInternetException
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
    ): Result<CreatedBook> {

        val result: Result<CreatedBook> = RequestUtils.validateErrors {
            val bookResponse = BookResponse(title, author, isbn, publisher, description, thumbnail)
            val response =
                api.createBook("Bearer $accessToken", bookResponse)
            val tempResult = RequestUtils.isResponseSuccessful(response)

            if (tempResult is Result.Success)
                Result.Success(tempResult.response)
            else
                tempResult as Result.Error
        }

        return result;
    }

    suspend fun list(
        accessToken: String,
        pageNum: Int,
    ): Result<List<CreatedBook>> {

        val result: Result<List<CreatedBook>> = RequestUtils.validateErrors {
            val response = api.bookList("Bearer $accessToken", pageNum)
            val tempResult = RequestUtils.isResponseSuccessful(response)

            if (tempResult is Result.Success)
                Result.Success(tempResult.response.books)
            else
                tempResult as Result.Error
        }

        return result;
    }

    suspend fun searchOnline(
        accessToken: String,
        searchFilter: String,
        lang: String = "pt"
    ): Result<List<Book>> {
        val result: Result<List<Book>> = RequestUtils.validateErrors {
            val response = api.searchBook("Bearer $accessToken", searchFilter, lang)

            val tempResult = RequestUtils.isResponseSuccessful(response)


            if (tempResult is Result.Success)
                Result.Success(tempResult.response.books)
            else
                tempResult as Result.Error
        }

        return result
    }
}