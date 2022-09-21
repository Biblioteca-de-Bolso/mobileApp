package com.bibliotecadebolso.app.ui.book.gridList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.BookDataSource
import com.bibliotecadebolso.app.data.model.CreatedBook
import com.bibliotecadebolso.app.data.model.ReadStatusEnum
import com.bibliotecadebolso.app.data.model.response.ErrorResponse
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.launch

class BookListViewModel : ViewModel() {

    val bookListLiveData: MutableLiveData<Result<List<CreatedBook>>> = MutableLiveData()
    var bookListPreviousSuccessResponse: MutableList<CreatedBook>? = null
    private var reachedOnTheEnd = false
    private var page: Int = 1

    fun getBookList(
        accessToken: String,
        pageNum: Int = -1,
        readStatusEnum: ReadStatusEnum? = null
    ) {
        viewModelScope.launch {
            if (bookListReachedOnTheEnd()) {
                bookListLiveData.postValue(Result.Error(null, reachedOnTheEndErrorResponse()))
                return@launch
            }

            val requestPageNum = if (pageNum == -1) page else pageNum
            val response = BookDataSource.list(accessToken, requestPageNum, readStatusEnum)

            bookListLiveData.postValue(handleBookListResponse(response))
            if (pageNum == -1 && !reachedOnTheEnd) page++
        }
    }

    private fun handleBookListResponse(response: Result<List<CreatedBook>>): Result<List<CreatedBook>> {
        if (response is Result.Success) {
            if (bookListPreviousSuccessResponse == null) {
                bookListPreviousSuccessResponse = response.response.toMutableList()
            } else {
                val oldList = bookListPreviousSuccessResponse!!
                val newList = response.response
                if (newList.isEmpty())
                    return Result.Error(null, reachedOnTheEndErrorResponse())
                oldList.addAll(newList)
            }
            return Result.Success(bookListPreviousSuccessResponse!!.toList())
        }

        return response

    }

    fun bookListReachedOnTheEnd(): Boolean {
        if (bookListPreviousSuccessResponse!= null && bookListPreviousSuccessResponse!!.size %10 != 0) return true

        return false
    }

    fun reachedOnTheEndErrorResponse() = ErrorResponse("error", "reachedOnTheEnd","list reached on the end")
}