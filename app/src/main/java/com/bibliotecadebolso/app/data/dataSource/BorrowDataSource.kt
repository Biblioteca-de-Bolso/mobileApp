package com.bibliotecadebolso.app.data.dataSource

import com.bibliotecadebolso.app.data.model.UpdateBook
import com.bibliotecadebolso.app.data.model.UpdatedBook
import com.bibliotecadebolso.app.data.model.request.Borrow
import com.bibliotecadebolso.app.data.model.request.CreateBorrow
import com.bibliotecadebolso.app.data.model.request.DeleteBorrow
import com.bibliotecadebolso.app.data.model.request.EditBorrow
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

    suspend fun createBorrow(accessToken: String, borrow: CreateBorrow): Result<Borrow> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.createBorrow("Bearer $accessToken", borrow)
            val responseResult = RequestUtils.convertAPIResponseToResultClass(response)
            if (responseResult is Result.Success)
                Result.Success(responseResult.response.borrow)
            else
                responseResult as Result.Error
        }
    }

    suspend fun listBorrow(accessToken: String, page: Int = -1,bookId: Int? = null, searchString: String? = null): Result<List<Borrow>> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.getBorrowList("Bearer $accessToken", page, bookId, searchString)
            val responseResult = RequestUtils.convertAPIResponseToResultClass(response)
            if (responseResult is Result.Success)
                Result.Success(responseResult.response.borrows)
            else
                responseResult as Result.Error
        }
    }

    suspend fun editBorrow(accessToken: String, editBorrow: EditBorrow): Result<Borrow> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.editBorrow("Bearer $accessToken",editBorrow)
            val responseResult = RequestUtils.convertAPIResponseToResultClass(response)
            if (responseResult is Result.Success)
                Result.Success(responseResult.response.borrow)
            else
                responseResult as Result.Error
        }
    }

    suspend fun deleteBorrow(accessToken: String, deleteBorrow: DeleteBorrow): Result<Nothing> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.deleteBorrow("Bearer $accessToken",deleteBorrow)
            val responseResult = RequestUtils.convertAPIResponseToResultClass(response)
            if (responseResult is Result.Success)
                Result.Success(responseResult.response)
            else
                responseResult as Result.Error
        }
    }
}