package com.bibliotecadebolso.app.ui.init

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.LoginDataSource
import com.bibliotecadebolso.app.data.model.AuthTokens
import com.bibliotecadebolso.app.util.ConnectivityHandler
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.connectivityScope
import kotlinx.coroutines.launch

class InitViewModel : ViewModel() {


    private val loginDataSource = LoginDataSource()
    val liveDataAuthTokens = MutableLiveData<Result<AuthTokens?>>()

    fun getNewAccessToken(accessToken: String, refreshToken: String) {
        viewModelScope.launch(ConnectivityHandler.handler) {
            connectivityScope(liveDataAuthTokens) {
                loginDataSource.getNewAccessToken(accessToken, refreshToken)
            }
        }
    }
}