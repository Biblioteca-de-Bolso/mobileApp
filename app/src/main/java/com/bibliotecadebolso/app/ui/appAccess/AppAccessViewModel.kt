package com.bibliotecadebolso.app.ui.appAccess

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.LoginDataSource
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.data.model.AuthTokens
import com.bibliotecadebolso.app.data.model.response.ErrorResponse
import com.bibliotecadebolso.app.data.model.response.UserObject
import com.bibliotecadebolso.app.data.validator.validations.EmailValidation
import com.bibliotecadebolso.app.data.validator.validations.PasswordValidator

import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class AppAccessViewModel : ViewModel() {

    private val loginDataSource = LoginDataSource()
    val loginResponse = MutableLiveData<Result<AuthTokens?>>()
    val registerResponse = MutableLiveData<Result<UserObject?>>()

    fun login(email: String, password: String): Boolean {
        viewModelScope.launch {
            val response = loginDataSource.login(email, password)
            loginResponse.postValue(response)
        }
        return true
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            val response = loginDataSource.register(username, email, password)
            registerResponse.postValue(response)
        }
    }

    // A placeholder username validation check
    fun isEmailValid(email: String): Boolean {
        return EmailValidation(email).validate().isSuccess
    }

    // A placeholder password validation check
    fun isPasswordValid(password: String): Boolean {
        return PasswordValidator(password).validate().isSuccess
    }
}