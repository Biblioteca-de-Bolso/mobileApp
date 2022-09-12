package com.bibliotecadebolso.app.ui.book.gridList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.BookDataSource
import com.bibliotecadebolso.app.data.model.CreatedBook
import com.bibliotecadebolso.app.data.model.ReadStatusEnum
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.launch

class BookListViewModel : ViewModel() {

    val bookListLiveData: MutableLiveData<Result<List<CreatedBook>>> = MutableLiveData()

    fun getBookList(accessToken: String, pageNum:Int = 1, readStatusEnum: ReadStatusEnum? = null) {
        viewModelScope.launch {
            val response = BookDataSource.list(accessToken, pageNum, readStatusEnum)

            bookListLiveData.postValue(response)
        }
    }
}