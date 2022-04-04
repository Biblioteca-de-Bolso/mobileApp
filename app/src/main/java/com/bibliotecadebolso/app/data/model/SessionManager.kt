package com.bibliotecadebolso.app.data.model

import android.content.Context
import android.content.SharedPreferences
import com.bibliotecadebolso.app.R

class SessionManager(context: Context) {

    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name),
        Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
    }

    fun saveAuthTokens(authTokens: AuthTokens) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN,
            authTokens.accessToken)
        editor.apply()
    }


    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN,
            null)
    }


}