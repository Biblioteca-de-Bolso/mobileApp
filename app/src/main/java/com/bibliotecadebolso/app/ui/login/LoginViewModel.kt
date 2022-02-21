package com.bibliotecadebolso.app.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.LoginDataSource
import com.bibliotecadebolso.app.data.Result
import com.bibliotecadebolso.app.data.model.AuthTokens

import kotlinx.coroutines.launch

class LoginViewModel() : ViewModel() {

    private val loginDataSource = LoginDataSource()
    val loginResponse = MutableLiveData<Result<AuthTokens?>>()
    val registerResponse = MutableLiveData<Result<String?>>()

    fun login(email: String, password: String) : Boolean {
        if (isEmailValid(email) && isPasswordValid(password)) {
            viewModelScope.launch {
                val response = loginDataSource.login(email, password)
                loginResponse.postValue(response)
            }
        } else {
            return false
        }
        return true
    }

    fun register(username: String, email: String, password: String) {
        if (isEmailValid(email) && isPasswordValid(password)) {
            viewModelScope.launch {
                val response = loginDataSource.register(username, email, password)
                registerResponse.postValue(response)
            }
        }
    }
    // A placeholder username validation check
    private fun isEmailValid(email: String): Boolean {
        if (email.isNotEmpty() && email.contains("@")) return true
        return false
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return true
    }
}