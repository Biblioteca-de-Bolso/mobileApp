package com.bibliotecadebolso.app.util

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.bibliotecadebolso.app.data.model.AuthTokens

object SharedPreferencesUtils {

    fun getAuthTokens(prefs: SharedPreferences): AuthTokens {
        val accessToken = prefs.getString(Constants.Prefs.Tokens.ACCESS_TOKEN, "")!!
        val refreshToken = prefs.getString(Constants.Prefs.Tokens.REFRESH_TOKEN, "")!!

        return AuthTokens(accessToken, refreshToken)
    }

    fun putAuthTokens(
        prefs: SharedPreferences,
        newAccessToken: String,
        newRefreshToken: String
    ) {
        val accessTokenLabel = Constants.Prefs.Tokens.ACCESS_TOKEN
        val refreshTokenLabel = Constants.Prefs.Tokens.REFRESH_TOKEN

        with(prefs.edit()) {
            putString(accessTokenLabel, newAccessToken)
            putString(refreshTokenLabel, newRefreshToken)
            apply()
        }
    }
}