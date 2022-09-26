package com.bibliotecadebolso.app.data.dataSource

import com.bibliotecadebolso.app.data.model.UpdateBook
import com.bibliotecadebolso.app.data.model.UpdatedBook
import com.bibliotecadebolso.app.data.repository.BibliotecaDeBolsoRepository
import com.bibliotecadebolso.app.util.RequestUtils
import com.bibliotecadebolso.app.util.Result

class BorrowDataSource {

    private val api = BibliotecaDeBolsoRepository.retrofit()

    suspend fun updateBookById(accessToken: String, book: UpdateBook): Result<UpdatedBook> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.updateBookById("Bearer $accessToken", book)
            val responseResult = RequestUtils.convertAPIResponseToResultClass(response)
            if (responseResult is Result.Success)
                Result.Success(responseResult.response.book)
            else
                responseResult as Result.Error
        }
    }
}