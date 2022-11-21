package com.bibliotecadebolso.app.ui.borrow.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.BookDataSource
import com.bibliotecadebolso.app.data.dataSource.BorrowDataSource
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.request.Borrow
import com.bibliotecadebolso.app.data.model.request.BorrowStatus
import com.bibliotecadebolso.app.data.model.request.DeleteBorrow
import com.bibliotecadebolso.app.data.model.request.EditBorrow
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.connectivityScope
import kotlinx.coroutines.launch

class EditBorrowViewModel : ViewModel() {

    private val borrowDataSource = BorrowDataSource()

    val editBorrowLiveData = MutableLiveData<Result<Borrow>>()
    val removeBorrowLiveData = MutableLiveData<Result<Boolean>>()
    val borrowLiveData = MutableLiveData<Result<Borrow>>()
    val bookLiveData = MutableLiveData<Result<Book>>()

    val inputs = Inputs()
    val spinner = Spinner()

    fun editBorrow(accessToken: String, editBorrow: EditBorrow) {
        viewModelScope.launch {
            connectivityScope(editBorrowLiveData) {
                borrowDataSource.editBorrow(accessToken, editBorrow)
            }
        }
    }

    fun removeBorrow(accessToken: String, borrowId: Int) {
        viewModelScope.launch {
            connectivityScope(removeBorrowLiveData) {
                borrowDataSource.deleteBorrow(accessToken, DeleteBorrow(borrowId))
            }
        }
    }

    fun getBorrowById(accessToken: String, borrowId: Int) {
        viewModelScope.launch {
            val result = borrowDataSource.getBorrowById(accessToken,borrowId)

            borrowLiveData.postValue(result)
        }
    }

    fun getBookById(accessToken: String, bookId: Int) {
        viewModelScope.launch {
            val result = BookDataSource.getBookById(accessToken, bookId)
            bookLiveData.postValue(result)
        }
    }


    inner class Spinner(
        val borrowStatusValuesKey: HashMap<String, String> = HashMap()
    ) {
        fun setReadStatusValuesKeyMap(
            spinnerBorrowStatusKey: Array<String>,
            spinnerBorrowStatusValue: Array<String>
        ) {
            borrowStatusValuesKey.clear()
            for (i in spinnerBorrowStatusKey.indices) {
                borrowStatusValuesKey[spinnerBorrowStatusValue[i]] = spinnerBorrowStatusKey[i]
            }
        }

        fun getReadingStatusKeyByIndex(indexOfBorrowStatusLabel: Int) =
            borrowStatusValuesKey.keys.toList()[indexOfBorrowStatusLabel]

        fun getReadStatusEnumIndexOnSpinner(borrowStatusEnum: BorrowStatus) =
            borrowStatusValuesKey.values.toList()
                .indexOf(borrowStatusEnum.toString())
    }

    inner class Inputs(
        var bookId: Int = -1,
        var contactName: String = "",
        var borrowStatusSelected:BorrowStatus = BorrowStatus.PENDING
    )
}