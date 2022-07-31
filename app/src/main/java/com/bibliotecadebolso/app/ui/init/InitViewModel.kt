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

    fun getNewAcessToken(refreshToken: String) {
        viewModelScope.launch {
            val result = loginDataSource.getNewAccessToken(refreshToken)
            liveDataAuthTokens.postValue(result)
        }
    }
}