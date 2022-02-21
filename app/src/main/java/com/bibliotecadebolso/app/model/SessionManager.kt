package com.bibliotecadebolso.app.model

import android.content.Context
import android.content.SharedPreferences
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.AuthTokens

class SessionManager(context: Context) {

    private var prefs: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
    }

    fun saveAuthTokens(authTokens: AuthTokens) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, authTokens.accesToken)
        editor.apply()
    }


    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }


}