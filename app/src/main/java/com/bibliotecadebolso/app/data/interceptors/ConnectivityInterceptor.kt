package com.bibliotecadebolso.app.data.interceptors

import android.util.Log
import com.bibliotecadebolso.app.data.model.exceptions.NoInternetException
import com.bibliotecadebolso.app.util.WifiService
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.Response
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class ConnectivityInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!WifiService.instance.isOnline()) {
            throw NoInternetException("No internet connection")
        } else {
            try {
                return chain.proceed(chain.request())
            } catch (e: SocketTimeoutException) {
                throw NoInternetException("Too long time request")
            } catch (e: TimeoutException) {
                throw NoInternetException("Too long time request")
            } catch (e: Throwable) {
                throw NoInternetException(e.message.toString())
            }
        }
    }
}