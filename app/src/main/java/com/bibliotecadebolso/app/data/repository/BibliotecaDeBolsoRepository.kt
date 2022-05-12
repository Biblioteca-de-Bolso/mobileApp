package com.bibliotecadebolso.app.data.repository

import com.bibliotecadebolso.app.data.api.LoginAPI
import com.bibliotecadebolso.app.data.interceptors.ConnectivityInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object BibliotecaDeBolsoRepository {

    private const val BASE_URL = "https://bibliotecadebolso.herokuapp.com/api/"

    private val headerBodyInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val client = OkHttpClient.Builder()
            .apply {
                addInterceptor(interceptor = headerBodyInterceptor)
                addInterceptor(ConnectivityInterceptor())
                connectTimeout(10, TimeUnit.SECONDS)
                writeTimeout(10, TimeUnit.SECONDS)
                readTimeout(30, TimeUnit.SECONDS)
            }
            .build()


    fun retrofit(buildApi: String? = null): LoginAPI = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(LoginAPI::class.java)
}