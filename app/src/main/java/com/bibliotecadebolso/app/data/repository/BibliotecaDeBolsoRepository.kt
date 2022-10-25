package com.bibliotecadebolso.app.data.repository

import com.bibliotecadebolso.app.data.api.BibliotecaDeBolsoAPI
import com.bibliotecadebolso.app.data.interceptors.ConnectivityInterceptor
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

object BibliotecaDeBolsoRepository {

    private const val BASE_URL = "https://bibliotecadebolso.herokuapp.com/api/"

    private val headerBodyInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val gson = com.google.gson.GsonBuilder().registerTypeAdapter(
        LocalDateTime::class.java,
        object : JsonDeserializer<LocalDateTime?> {
            @Throws(JsonParseException::class)
            override fun deserialize(
                json: JsonElement,
                type: Type?,
                jsonDeserializationContext: JsonDeserializationContext?
            ): LocalDateTime? {
                return LocalDateTime.parse(
                    json.asJsonPrimitive.asString,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.[SSS][SS]'Z'")
                )
            }
        })
        .setLenient()
        .create()


    private val client = OkHttpClient.Builder()
        .apply {
            addInterceptor(interceptor = headerBodyInterceptor)
            addInterceptor(ConnectivityInterceptor())
            connectTimeout(10, TimeUnit.SECONDS)
            writeTimeout(10, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
        }
        .build()


    fun retrofit(buildApi: String? = null): BibliotecaDeBolsoAPI = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()
        .create(BibliotecaDeBolsoAPI::class.java)
}