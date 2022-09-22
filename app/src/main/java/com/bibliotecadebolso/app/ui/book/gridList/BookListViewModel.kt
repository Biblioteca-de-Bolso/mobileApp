package com.bibliotecadebolso.app.ui.book.gridList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.BookDataSource
import com.bibliotecadebolso.app.data.model.CreatedBook
import com.bibliotecadebolso.app.data.model.ReadStatusEnum
import com.bibliotecadebolso.app.data.model.app.list.ListContent
import com.bibliotecadebolso.app.data.model.app.list.SearchListContent
import com.bibliotecadebolso.app.data.model.exceptions.ListReachedOnTheEndException
import com.bibliotecadebolso.app.data.model.response.ErrorResponse
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.launch

class BookListViewModel : ViewModel() {

    var listType: ListType = ListType.NORMAL_LIST

    val searchList = SearchListContent<CreatedBook>()
    val normalList = ListContent<CreatedBook>()

    fun getBookList(
        accessToken: String,
        pageNum: Int = -1,
        readStatusEnum: ReadStatusEnum? = null
    ) {
        viewModelScope.launch {
            requestGetContent(normalList, accessToken, pageNum, readStatusEnum)
        }
    }

    fun searchBook(
        accessToken: String,
        searchContent: String,
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
        listContent: ListContent<CreatedBook>,
        accessToken: String,
        pageNum: Int = -1,
        readStatusEnum: ReadStatusEnum? = null,
        searchContent: String? = null,
        isNewSearchContent: Boolean = false,
    ) {
        try {
            if (listContent is SearchListContent && isNewSearchContent) {
                if (isNewSearchContent) listContent.page = 1
            } else {
                throwIfListReachedOnTheEnd(listContent)
            }

            val page = if (isInvalidPage(pageNum)) listContent.page else pageNum

            val response = BookDataSource.list(accessToken, page, readStatusEnum, searchContent)

            if (listContent is SearchListContent) {
                listContent.bookListLiveData.postValue(
                    listContent.handleBookListResponse(response, isNewSearchContent)
                )
            } else {
                listContent.bookListLiveData.postValue(listContent.handleBookListResponse(response))
            }

            if (usedInternalPageNum(pageNum) && !listContent.reachedOnTheEnd) listContent.page++
        } catch (e: ListReachedOnTheEndException) {
            listContent.bookListLiveData.postValue(
                Result.Error(
                    null,
                    reachedOnTheEndErrorResponse()
                )
            )
        }
    }

    private fun isInvalidPage(pageNum: Int) = pageNum == -1

    private fun usedInternalPageNum(pageNum: Int): Boolean = isInvalidPage(pageNum)

    private fun throwIfListReachedOnTheEnd(listContent: ListContent<CreatedBook>) {
        if (bookListReachedOnTheEnd()) throw ListReachedOnTheEndException()
    }

    fun bookListReachedOnTheEnd(): Boolean {
        return when (listType) {
            ListType.NORMAL_LIST -> normalList.bookListReachedOnTheEnd()
            ListType.SEARCH -> searchList.bookListReachedOnTheEnd()
        }
    }

    companion object {
        fun reachedOnTheEndErrorResponse() =
            ErrorResponse("error", "reachedOnTheEnd", "list reached on the end")
    }
}

enum class ListType {
    SEARCH,
    NORMAL_LIST
}

data class SearchContent(
    var searchContent: String = ""
) : ListContent<CreatedBook>() {

    fun handleBookListResponse(
        response: Result<List<CreatedBook>>,
        isNewSearchContent: Boolean = false
    ): Result<List<CreatedBook>> {
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