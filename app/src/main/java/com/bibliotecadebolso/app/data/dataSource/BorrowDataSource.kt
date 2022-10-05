package com.bibliotecadebolso.app.data.dataSource

import com.bibliotecadebolso.app.data.model.UpdateBook
import com.bibliotecadebolso.app.data.model.UpdatedBook
import com.bibliotecadebolso.app.data.model.request.*
import com.bibliotecadebolso.app.data.repository.BibliotecaDeBolsoRepository
import com.bibliotecadebolso.app.util.RequestUtils
import com.bibliotecadebolso.app.util.Result

class BorrowDataSource {

    private val api = BibliotecaDeBolsoRepository.retrofit()

    suspend fun getBorrowById(accessToken: String, borrowId: Int): Result<Borrow> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.getBorrowById("Bearer $accessToken", borrowId)
            val responseResult = RequestUtils.convertAPIResponseToResultClass(response)
            if (responseResult is Result.Success)
                Result.Success(responseResult.response.borrow)
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

    suspend fun listBorrow(
        accessToken: String,
        page: Int = -1,
        bookId: Int? = null,
        searchString: String? = null,
        borrowStatus: BorrowStatus? = null
    ): Result<List<Borrow>> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response =
                api.getBorrowList("Bearer $accessToken", page, bookId, searchString, borrowStatus)
            val responseResult = RequestUtils.convertAPIResponseToResultClass(response)
            if (responseResult is Result.Success)
                Result.Success(responseResult.response.borrows)
            else
                responseResult as Result.Error
        }
    }

    suspend fun editBorrow(accessToken: String, editBorrow: EditBorrow): Result<Borrow> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.editBorrow("Bearer $accessToken", editBorrow)
            val responseResult = RequestUtils.convertAPIResponseToResultClass(response)
            if (responseResult is Result.Success)
                Result.Success(responseResult.response.borrow)
            else
                responseResult as Result.Error
        }
    }

    suspend fun deleteBorrow(accessToken: String, deleteBorrow: DeleteBorrow): Result<Boolean> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.deleteBorrow("Bearer $accessToken", deleteBorrow)
            val responseResult = RequestUtils.convertAPIResponseToResultClass(response)
            if (responseResult is Result.Success)
                Result.Success(true)
            else
                responseResult as Result.Error
        }
    }
}