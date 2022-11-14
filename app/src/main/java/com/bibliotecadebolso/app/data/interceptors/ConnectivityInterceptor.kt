package com.bibliotecadebolso.app.data.interceptors

import android.content.Context
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.application.MainApplication
import com.bibliotecadebolso.app.data.model.exceptions.NoInternetException
import com.bibliotecadebolso.app.util.WifiService
import okhttp3.Interceptor
import okhttp3.Response
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class ConnectivityInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        
        val context = MainApplication.instance as Context
        
        if (!WifiService.instance.isOnline()) {
            throw NoInternetException(context.getString(R.string.label_no_internet_connection))
        } else {
            try {
                return chain.proceed(chain.request())
            } catch (e: SocketTimeoutException) {
                throw NoInternetException(context.getString(R.string.label_too_long_time_request))
            } catch (e: TimeoutException) {
                throw NoInternetException(context.getString(R.string.label_too_long_time_request))
            } catch (e: Throwable) {
                throw NoInternetException(e.message.toString())
            }
        }
    }
}