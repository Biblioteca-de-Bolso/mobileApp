package com.bibliotecadebolso.app.data.dataSource

import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.CreatedBook
import com.bibliotecadebolso.app.data.model.response.BookResponse
import com.bibliotecadebolso.app.data.repository.BibliotecaDeBolsoRepository
import com.bibliotecadebolso.app.util.RequestUtils
import com.bibliotecadebolso.app.util.Result

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

        val result: Result<CreatedBook> = RequestUtils.returnOrThrowIfHasConnectionError {
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

        val result: Result<List<CreatedBook>> = RequestUtils.returnOrThrowIfHasConnectionError {
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
        val result: Result<List<Book>> = RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.searchBook("Bearer $accessToken", searchFilter, lang)

            val tempResult = RequestUtils.isResponseSuccessful(response)


            if (tempResult is Result.Success)
                Result.Success(tempResult.response.books)
            else
                tempResult as Result.Error
        }

        return result
    }

    suspend fun getBookById(accessToken: String, id: Int): Result<Book> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.getBookById("Bearer $accessToken", id)
            val responseResult = RequestUtils.isResponseSuccessful(response)
            if (responseResult is Result.Success)
                Result.Success(responseResult.response.book)
            else
                responseResult as Result.Error
        }

    }
}