package com.bibliotecadebolso.app.ui.book.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.BookDataSource
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.ReadStatusEnum
import com.bibliotecadebolso.app.data.model.UpdateBook
import com.bibliotecadebolso.app.data.model.UpdatedBook
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.launch

class EditBookViewModel : ViewModel() {

    val bookLiveData = MutableLiveData<Result<Book>>()
    val updatedBookLiveData = MutableLiveData<Result<UpdatedBook>>()
    var lastReadingStatus = ReadStatusEnum.PLANNING
        private set
    var bookThumbnail = ""

    fun getBookById(accessToken: String, bookId: Long) {
        viewModelScope.launch {
            val result = BookDataSource.getBookById(accessToken, bookId.toInt())
            if (result is Result.Success) {
                lastReadingStatus = result.response.readStatus ?: ReadStatusEnum.PLANNING
            }

            bookLiveData.postValue(result)
        }
    }


    fun updateBook(accessToken: String, updateBook: UpdateBook) {
        viewModelScope.launch {
            val result = BookDataSource.updateBookById(accessToken, updateBook)

            updatedBookLiveData.postValue(result)
        }
    }
}