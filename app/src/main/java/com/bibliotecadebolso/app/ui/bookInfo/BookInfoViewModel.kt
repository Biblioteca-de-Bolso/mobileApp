package com.bibliotecadebolso.app.ui.bookInfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.BookDataSource
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.launch

class BookInfoViewModel : ViewModel() {

    val liveDataBookInfo = MutableLiveData<Result<Book>>()
    var isDescriptionShowMoreActive = false
    val liveDataDeleteBook = MutableLiveData<Result<String>>()
    val dataSource = BookDataSource


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

}