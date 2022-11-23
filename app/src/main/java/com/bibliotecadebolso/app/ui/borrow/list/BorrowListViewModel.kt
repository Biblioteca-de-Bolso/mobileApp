package com.bibliotecadebolso.app.ui.borrow.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.BorrowDataSource
import com.bibliotecadebolso.app.data.model.app.list.SearchListContent
import com.bibliotecadebolso.app.data.model.exceptions.ListReachedOnTheEndException
import com.bibliotecadebolso.app.data.model.request.Borrow
import com.bibliotecadebolso.app.data.model.request.BorrowStatus
import com.bibliotecadebolso.app.data.model.response.ErrorResponse
import com.bibliotecadebolso.app.ui.book.linearList.BookListViewModel
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.connectivityScope
import kotlinx.coroutines.launch

class BorrowListViewModel: ViewModel() {

    val borrowDataSource = BorrowDataSource()

    val searchList = SearchListContent<Borrow>()

    val liveDataBorrowById = MutableLiveData<Result<Borrow>>()

    fun getBorrowById(accessToken: String, borrowId: Int) {
        viewModelScope.launch {
            connectivityScope(liveDataBorrowById) {
                borrowDataSource.getBorrowById(accessToken, borrowId)
            }
        }
    }



    fun searchListBorrow(
        accessToken: String,
        searchContent: String?,
        pageNum: Int = -1,
        bookId: Int? = null,
        borrowStatusEnum: BorrowStatus? = null,
        newSearchContent: Boolean = false
    ) {
        viewModelScope.launch {
            requestGetContent(
                searchList,
                accessToken,
                pageNum,
                borrowStatusEnum,
                bookId,
                searchContent,
                newSearchContent
            )
        }
    }

    private suspend fun requestGetContent(
        listContent: SearchListContent<Borrow>,
        accessToken: String,
        pageNum: Int = -1,
        borrowStatusEnum: BorrowStatus? = null,
        bookId: Int? = null,
        searchContent: String? = null,
        isNewSearchContent: Boolean = false,
    ) {
        try {
            if (isNewSearchContent) listContent.page = 1
            else throwIfListReachedOnTheEnd()

            val page = if (isInvalidPage(pageNum)) listContent.page else pageNum
            val response = borrowDataSource.listBorrow(accessToken, page, bookId, searchContent, borrowStatusEnum)

            connectivityScope(listContent.listLiveData) {
                listContent.handleBookListResponse(response, isNewSearchContent)
            }

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
            BookListViewModel.reachedOnTheEndErrorResponse()
    }
}