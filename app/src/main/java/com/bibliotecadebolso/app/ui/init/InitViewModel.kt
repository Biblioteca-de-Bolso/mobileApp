package com.bibliotecadebolso.app.ui.init

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.LoginDataSource
import com.bibliotecadebolso.app.data.model.AuthTokens
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.launch

class InitViewModel : ViewModel() {


    private val loginDataSource = LoginDataSource()
    val liveDataAuthTokens = MutableLiveData<Result<AuthTokens?>>()

    fun getNewAccessToken(accessToken: String, refreshToken: String) {
        viewModelScope.launch {
            val result = loginDataSource.getNewAccessToken(accessToken, refreshToken)
            liveDataAuthTokens.postValue(result)
        }
    }
}