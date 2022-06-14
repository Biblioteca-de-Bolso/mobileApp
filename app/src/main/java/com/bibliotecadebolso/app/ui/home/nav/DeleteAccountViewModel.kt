package com.bibliotecadebolso.app.ui.home.nav

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.LoginDataSource
import com.bibliotecadebolso.app.data.model.DeleteForm
import com.bibliotecadebolso.app.data.model.response.DeleteAccountResponse
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.launch

class DeleteAccountViewModel : ViewModel() {

    val deleteAccountResult = MutableLiveData<Result<DeleteAccountResponse>>()
    val dataSource = LoginDataSource()

    fun deleteAccount(accessToken: String, id: Int, email: String, password: String) {
        val deleteForm = DeleteForm(id, email, password)
        viewModelScope.launch {
            val result = dataSource.deleteAccount(accessToken, deleteForm)
            deleteAccountResult.postValue(result)
        }

    }


}