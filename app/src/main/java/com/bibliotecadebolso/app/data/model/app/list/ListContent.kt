package com.bibliotecadebolso.app.data.model.app.list

import androidx.lifecycle.MutableLiveData
import com.bibliotecadebolso.app.data.model.CreatedBook
import com.bibliotecadebolso.app.ui.book.gridList.BookListViewModel
import com.bibliotecadebolso.app.util.Result

open class ListContent<T>(
    val bookListLiveData: MutableLiveData<Result<List<T>>> = MutableLiveData(),
    var bookListPreviousSuccessResponse: MutableList<T>? = null,
    val reachedOnTheEnd: Boolean = false,
    var page: Int = 1,
) {

    fun handleBookListResponse(response: Result<List<T>>): Result<List<T>> {
        if (response is Result.Success) {
            if (bookListPreviousSuccessResponse == null) {
                bookListPreviousSuccessResponse = response.response.toMutableList()
            } else {
                bookListPreviousSuccessResponse!!
                val newList = response.response
                if (newList.isEmpty())
                    return Result.Error(null, BookListViewModel.reachedOnTheEndErrorResponse())
                bookListPreviousSuccessResponse!!.addAll(newList)
            }
            return Result.Success(bookListPreviousSuccessResponse!!.toList())
        }

        return response

    }

    fun bookListReachedOnTheEnd(): Boolean {
        if (bookListPreviousSuccessResponse != null && bookListPreviousSuccessResponse!!.size % 10 != 0) return true

        return false
    }

}