package com.bibliotecadebolso.app.data.model.app.list

import com.bibliotecadebolso.app.data.model.CreatedBook
import com.bibliotecadebolso.app.ui.book.gridList.BookListViewModel
import com.bibliotecadebolso.app.util.Result

open class SearchListContent<T> (
    var searchContent: String = ""
) : ListContent<T>() {

    fun handleBookListResponse(
        response: Result<List<T>>,
        isNewSearchContent: Boolean = false
    ): Result<List<T>> {
        if (response is Result.Success) {
            if (bookListPreviousSuccessResponse == null || isNewSearchContent) {
                bookListPreviousSuccessResponse = response.response.toMutableList()
            } else {
                val oldList = bookListPreviousSuccessResponse!!
                val newList = response.response
                if (newList.isEmpty())
                    return Result.Error(null, BookListViewModel.reachedOnTheEndErrorResponse())
                oldList.addAll(newList)
            }
            return Result.Success(bookListPreviousSuccessResponse!!.toList())
        }

        return response

    }

}