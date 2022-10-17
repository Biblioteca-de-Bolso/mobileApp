package com.bibliotecadebolso.app.ui.book.linearList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.BookDataSource
import com.bibliotecadebolso.app.data.model.CreatedBook
import com.bibliotecadebolso.app.data.model.ReadStatusEnum
import com.bibliotecadebolso.app.data.model.app.list.SearchListContent
import com.bibliotecadebolso.app.data.model.exceptions.ListReachedOnTheEndException
import com.bibliotecadebolso.app.data.model.response.ErrorResponse
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.launch

class BookListViewModel : ViewModel() {

    val searchList = SearchListContent<CreatedBook>()

    fun searchBook(
        accessToken: String,
        searchContent: String?,
        pageNum: Int = -1,
        readStatusEnum: ReadStatusEnum? = null,
        newSearchContent: Boolean = false
    ) {
        viewModelScope.launch {
            requestGetContent(
                searchList,
                accessToken,
                pageNum,
                readStatusEnum,
                searchContent,
                newSearchContent
            )
        }
    }

    private suspend fun requestGetContent(
        listContent: SearchListContent<CreatedBook>,
        accessToken: String,
        pageNum: Int = -1,
        readStatusEnum: ReadStatusEnum? = null,
        searchContent: String? = null,
        isNewSearchContent: Boolean = false,
    ) {
        try {
            if (isNewSearchContent) listContent.page = 1
            else throwIfListReachedOnTheEnd()

            val page = if (isInvalidPage(pageNum)) listContent.page else pageNum
            val response = BookDataSource.list(accessToken, page, readStatusEnum, searchContent)

            listContent.listLiveData.postValue(
                listContent.handleBookListResponse(response, isNewSearchContent)
            )

            if (isInvalidPage(pageNum) && !listContent.reachedOnTheEnd) listContent.page++
        } catch (e: ListReachedOnTheEndException) {
            listContent.listLiveData.postValue(
                Result.Error(
                    null,
                    reachedOnTheEndErrorResponse()
                )
            )
        }
    }

    private fun isInvalidPage(pageNum: Int) = pageNum == -1

    private fun throwIfListReachedOnTheEnd() {
        if (bookListReachedOnTheEnd()) throw ListReachedOnTheEndException()
    }

    fun bookListReachedOnTheEnd(): Boolean {
        return searchList.bookListReachedOnTheEnd()
    }

    companion object {
        fun reachedOnTheEndErrorResponse() =
            ErrorResponse("error", "reachedOnTheEnd", "list reached on the end")
    }
}