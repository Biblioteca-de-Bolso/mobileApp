package com.bibliotecadebolso.app.data.api

import com.bibliotecadebolso.app.data.model.AuthTokens
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.response.APIResponse
import com.bibliotecadebolso.app.data.model.response.BookResponse
import retrofit2.Response
import retrofit2.http.*

interface LoginAPI {

    // BASE URL: http://bibliotecadebolso.herokuapp.com/api/

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Response<APIResponse<AuthTokens>>

    @FormUrlEncoded
    @POST("user")
    suspend fun register(
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("password") password: String,
    ): Response<String>

    @GET("book")
    fun bookList(@Header("Authorization") accessToken: String)

    @POST("book")
    suspend fun createBook(
        @Header("Authorization") accessToken: String,
        @Body bookResponse: BookResponse
    ) : Response<APIResponse<Book>>


}