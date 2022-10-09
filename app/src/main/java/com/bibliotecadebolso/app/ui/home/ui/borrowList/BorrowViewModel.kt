package com.bibliotecadebolso.app.ui.home.ui.borrowList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.BorrowDataSource
import com.bibliotecadebolso.app.data.model.request.Borrow
import com.bibliotecadebolso.app.data.model.request.BorrowStatus
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.launch

class BorrowViewModel : ViewModel() {

    val borrowDataSource = BorrowDataSource()
    val pendingBorrowListLiveData = MutableLiveData<Result<List<Borrow>>>()
    val returnedBorrowListLiveData = MutableLiveData<Result<List<Borrow>>>()

    fun getPendingBorrowList(accessToken: String) {
        viewModelScope.launch {
            val result = borrowDataSource.listBorrow(accessToken, page = 1,borrowStatus = BorrowStatus.PENDING)
            pendingBorrowListLiveData.postValue(result)
        }
    }

    fun getReturnedBorrowList(accessToken: String) {
        viewModelScope.launch {
            val result = borrowDataSource.listBorrow(accessToken, page = 1, borrowStatus = BorrowStatus.RETURNED)
            returnedBorrowListLiveData.postValue(result)
        }
    }

}