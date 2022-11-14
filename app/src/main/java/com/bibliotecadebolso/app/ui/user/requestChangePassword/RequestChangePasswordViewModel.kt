package com.bibliotecadebolso.app.ui.user.requestChangePassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.LoginDataSource
import com.bibliotecadebolso.app.ui.user.requestChangePassword.form.RequestChangePasswordForm
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.connectivityScope
import kotlinx.coroutines.launch

class RequestChangePasswordViewModel : ViewModel() {

    val dataSource = LoginDataSource()
    val requestChangePasswordLiveData = MutableLiveData<Result<Boolean>>()
    val changePasswordLiveData = MutableLiveData<Result<Boolean>>()


    fun requestChangePassword(requestChangePasswordForm: RequestChangePasswordForm) {
        viewModelScope.launch {
            connectivityScope(requestChangePasswordLiveData) {
                dataSource.requestChangePassword(requestChangePasswordForm)
            }
        }
    }

    fun changePassword(email: String, recoverCode: String, newPassword: String) {
        viewModelScope.launch {
            connectivityScope(changePasswordLiveData) {
                dataSource.changePassword(ChangePasswordForm(email, recoverCode, newPassword))
            }
        }
    }
}

