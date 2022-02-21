package com.bibliotecadebolso.app.data.api

import com.bibliotecadebolso.app.data.model.AuthTokens
import retrofit2.Response
import retrofit2.http.*

interface LoginAPI {

    // BASE URL: http://bibliotecadebolso.herokuapp.com/api/

    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Response<AuthTokens>

    @FormUrlEncoded
    @POST("user")
    fun signUp(
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("password") password: String
    ) : Response<String>

    @GET("book/list")
    fun bookList(@Header("x-access-token") authToken: String)



}