package com.bibliotecadebolso.app.ui.borrow.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.BookDataSource
import com.bibliotecadebolso.app.data.dataSource.BorrowDataSource
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.request.Borrow
import com.bibliotecadebolso.app.data.model.request.CreateBorrow
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.launch

class AddBorrowViewModel: ViewModel() {

    val borrowDataSource = BorrowDataSource()

    val bookLiveData = MutableLiveData<Result<Book>>()
    val createBorrowLiveData = MutableLiveData<Result<Borrow>>()
    var lastSelectedBookId = MutableLiveData<Int>(-1)
    fun getBookById(accessToken: String, bookId: Int) {
        viewModelScope.launch {
            val result = BookDataSource.getBookById(accessToken, bookId)
            bookLiveData.postValue(result)
        }
    }

    fun addBorrow(accessToken: String, borrow: CreateBorrow) {
        viewModelScope.launch {
            val result  = borrowDataSource.createBorrow(accessToken, borrow)

            createBorrowLiveData.postValue(result)
        }
    }


}