package com.bibliotecadebolso.app.ui.appAccess

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.LoginDataSource
import com.bibliotecadebolso.app.data.model.AuthTokens
import com.bibliotecadebolso.app.data.model.response.UserObject
import com.bibliotecadebolso.app.data.validator.validations.EmailValidation
import com.bibliotecadebolso.app.data.validator.validations.PasswordValidator
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.connectivityScope
import kotlinx.coroutines.launch

class AppAccessViewModel : ViewModel() {

    private val loginDataSource = LoginDataSource()
    val loginResponse = MutableLiveData<Result<AuthTokens?>>()
    val registerResponse = MutableLiveData<Result<UserObject?>>()

    val singUpInputs = SingUpInputs()


    fun login(email: String, password: String): Boolean {
        viewModelScope.launch {
            connectivityScope(loginResponse) {
                loginDataSource.login(email, password)
            }
        }
        return true
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            connectivityScope(registerResponse) {
                loginDataSource.register(username, email, password)
            }
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

    inner class SingUpInputs(
        var username: String = "",
        var email: String = "",
        var password: String = "",
        var confirmPassword: String = ""
        )
}