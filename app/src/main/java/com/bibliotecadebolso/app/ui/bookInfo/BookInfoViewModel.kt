package com.bibliotecadebolso.app.ui.bookInfo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.BookDataSource
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.UpdateBook
import com.bibliotecadebolso.app.data.model.UpdatedBook
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.launch

class BookInfoViewModel : ViewModel() {

    val liveDataBookInfo = MutableLiveData<Result<Book>>()
    var isDescriptionShowMoreActive = false
    val liveDataDeleteBook = MutableLiveData<Result<String>>()
    val liveDataUpdateBook = MutableLiveData<Result<UpdatedBook>>()
    val dataSource = BookDataSource

    var isToShowConfirmationDisplay = false
        private set

    fun setDisplayStatusConfirmation(boolean: Boolean) {
        Log.e("onViewModel", boolean.toString())
        isToShowConfirmationDisplay = boolean
    }


    fun getInfoByID(accessToken: String, id: Int) {
        viewModelScope.launch {
            val result = dataSource.getBookById(accessToken, id)
            liveDataBookInfo.postValue(result)
        }
    }

    fun deleteBook(accessToken: String, bookId: Int) {
        viewModelScope.launch {
            val result = dataSource.deleteBookById(accessToken, bookId)
            liveDataDeleteBook.postValue(result)
        }
    }

    fun updateBook(accessToken: String, book: UpdateBook) {
        viewModelScope.launch {
            val result = dataSource.updateBookById(accessToken, book)

            liveDataUpdateBook.postValue(result)
        }
    }

}