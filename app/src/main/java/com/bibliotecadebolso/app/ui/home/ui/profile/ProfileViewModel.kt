package com.bibliotecadebolso.app.ui.home.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import com.bibliotecadebolso.app.data.dataSource.LoginDataSource
import com.bibliotecadebolso.app.data.model.response.ProfileResponse
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val datasource = LoginDataSource()
    val profileLiveData: MutableLiveData<Result<ProfileResponse>> = MutableLiveData()

    fun getProfile(accessToken: String) {
        val accessTokenDecoded = JWT(accessToken)

        val userId = accessTokenDecoded.getClaim("id").asInt()
        Log.e("ProfileViewModel", "userId: $userId")

        viewModelScope.launch {
            val response = datasource.viewProfile(accessToken, userId!!.toInt())
            profileLiveData.postValue(response)
        }
    }
}