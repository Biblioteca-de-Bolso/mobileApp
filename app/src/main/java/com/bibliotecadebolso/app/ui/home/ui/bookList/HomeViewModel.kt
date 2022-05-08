package com.bibliotecadebolso.app.ui.home.ui.bookList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.BookDataSource
import com.bibliotecadebolso.app.data.model.CreatedBook
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {


    val bookList = MutableLiveData<Result<List<CreatedBook>>>()

    fun apiListBook(accessToken: String, pageNum:Int = 1) {
        viewModelScope.launch {
            val response = BookDataSource.list(accessToken, pageNum)
            bookList.postValue(response)
        }
    }
}