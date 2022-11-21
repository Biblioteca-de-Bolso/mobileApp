package com.bibliotecadebolso.app.ui.book.bookInfo.annotationList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.AnnotationDataSource
import com.bibliotecadebolso.app.data.model.Annotation
import com.bibliotecadebolso.app.data.model.AnnotationObject
import com.bibliotecadebolso.app.data.model.app.list.SearchListContent
import com.bibliotecadebolso.app.data.model.exceptions.ListReachedOnTheEndException
import com.bibliotecadebolso.app.data.model.response.ErrorResponse
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.connectivityScope
import kotlinx.coroutines.launch

class AnnotationListViewModel : ViewModel() {

    val searchList = SearchListContent<Annotation>()
    val liveDataAnnotationById = MutableLiveData<Result<AnnotationObject>>()

    fun getAnnotationById(accessToken: String, annotationId: Int) {
        viewModelScope.launch {
            connectivityScope(liveDataAnnotationById) {
                AnnotationDataSource.getById(accessToken, annotationId)
            }
        }
    }

    fun searchAnnotations(
        accessToken: String,
        bookId: Int,
        searchContent: String? = null,
        pageNum: Int = -1,
        newSearchContent: Boolean = false
    ) {
        viewModelScope.launch {
            requestGetContent(
                searchList,
                accessToken,
                pageNum,
                searchContent,
                newSearchContent,
                bookId,
            )
        }
    }

    private suspend fun requestGetContent(
        listContent: SearchListContent<Annotation>,
        accessToken: String,
        pageNum: Int = -1,
        searchContent: String? = null,
        isNewSearchContent: Boolean = false,
        bookId: Int,
    ) {
        try {
            if (isNewSearchContent) listContent.page = 1
            else throwIfListReachedOnTheEnd()

            val page = if (isInvalidPage(pageNum)) listContent.page else pageNum
            val response = AnnotationDataSource.getList(accessToken, page = page, searchContent = searchContent, bookId = bookId)

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