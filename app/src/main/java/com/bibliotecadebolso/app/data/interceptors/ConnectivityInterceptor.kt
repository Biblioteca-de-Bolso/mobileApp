package com.bibliotecadebolso.app.data.interceptors

import com.bibliotecadebolso.app.data.model.exceptions.NoInternetException
import com.bibliotecadebolso.app.util.RequestUtils
import com.bibliotecadebolso.app.util.WifiService
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ConnectivityInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!WifiService.instance.isOnline()) {
            throw NoInternetException("No internet connection")
        } else {
            return chain.proceed(chain.request())
        }
    }
}