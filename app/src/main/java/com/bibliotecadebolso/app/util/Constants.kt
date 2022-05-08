package com.bibliotecadebolso.app.util

object Constants {

    object Prefs {
        const val USER_TOKENS = "com.bibliotecadebolso.app.USER_TOKENS";
        object Tokens {
            const val ACCESS_TOKEN = "accessToken";
            const val REFRESH_TOKEN = "refreshToken"
        }
    }

    const val SEARCH_NEWS_DELAY = 500L

}