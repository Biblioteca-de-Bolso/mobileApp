package com.bibliotecadebolso.app.data.model.app.list

import com.bibliotecadebolso.app.ui.book.linearList.BookListViewModel
import com.bibliotecadebolso.app.util.Result

open class SearchListContent<T> (
    var searchContent: String? = null
) : ListContent<T>() {

    fun handleBookListResponse(
        response: Result<List<T>>,
        isNewSearchContent: Boolean = false
    ): Result<List<T>> {
        if (response is Result.Success) {
            if (listLastSucessfullyResponse == null || isNewSearchContent) {
                listLastSucessfullyResponse = response.response.toMutableList()
            } else {
                val oldList = listLastSucessfullyResponse!!
                val newList = response.response
                if (newList.isEmpty())
                    return Result.Error(null, BookListViewModel.reachedOnTheEndErrorResponse())
                oldList.addAll(newList)
            }
            return Result.Success(listLastSucessfullyResponse!!.toList())
        }

        return response

    }

}