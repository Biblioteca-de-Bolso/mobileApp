package com.bibliotecadebolso.app.data.model.app.list

import androidx.lifecycle.MutableLiveData
import com.bibliotecadebolso.app.ui.book.linearList.BookListViewModel
import com.bibliotecadebolso.app.util.Result

open class ListContent<T>(
    val listLiveData: MutableLiveData<Result<List<T>>> = MutableLiveData(),
    var listLastSucessfullyResponse: MutableList<T>? = null,
    val reachedOnTheEnd: Boolean = false,
    var page: Int = 1,
) {

    fun handleBookListResponse(response: Result<List<T>>): Result<List<T>> {
        if (response is Result.Success) {
            if (listLastSucessfullyResponse == null) {
                listLastSucessfullyResponse = response.response.toMutableList()
            } else {
                listLastSucessfullyResponse!!
                val newList = response.response
                if (newList.isEmpty())
                    return Result.Error(null, BookListViewModel.reachedOnTheEndErrorResponse())
                listLastSucessfullyResponse!!.addAll(newList)
            }
            return Result.Success(listLastSucessfullyResponse!!.toList())
        }

        return response

    }

    fun bookListReachedOnTheEnd(): Boolean {
        if (listLastSucessfullyResponse != null) {
            if (listLastSucessfullyResponse!!.isEmpty()) return true
            if (listLastSucessfullyResponse!!.size % 10 != 0) return true
        }

        return false
    }

}