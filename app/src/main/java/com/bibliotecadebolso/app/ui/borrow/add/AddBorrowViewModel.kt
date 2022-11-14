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
import com.bibliotecadebolso.app.util.connectivityScope
import kotlinx.coroutines.launch

class AddBorrowViewModel: ViewModel() {

    val borrowDataSource = BorrowDataSource()

    val bookLiveData = MutableLiveData<Result<Book>>()
    val createBorrowLiveData = MutableLiveData<Result<Borrow>>()
    var lastSelectedBookId = MutableLiveData<Int>(-1)

    val inputs = Inputs()
    fun getBookById(accessToken: String, bookId: Int) {
        viewModelScope.launch {
            connectivityScope(bookLiveData) {
                BookDataSource.getBookById(accessToken, bookId)
            }
        }
    }


    fun addBorrow(accessToken: String, borrow: CreateBorrow) {
        viewModelScope.launch {
            connectivityScope(createBorrowLiveData) {
                borrowDataSource.createBorrow(accessToken, borrow)
            }
        }
    }

    data class Inputs(
        var contactName:String = ""
    )

}

