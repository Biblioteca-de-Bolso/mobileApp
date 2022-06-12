package com.bibliotecadebolso.app.data.api

import com.bibliotecadebolso.app.data.model.*
import com.bibliotecadebolso.app.data.model.Annotation
import com.bibliotecadebolso.app.data.model.response.APIResponse
import com.bibliotecadebolso.app.data.model.response.AnnotationResponse
import com.bibliotecadebolso.app.data.model.response.BookResponse
import com.bibliotecadebolso.app.data.model.response.UserObject
import retrofit2.Response
import retrofit2.http.*

interface BibliotecaDeBolsoAPI {

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
    ): Response<APIResponse<UserObject>>

    @GET("book")
    suspend fun bookList(
        @Header("Authorization") accessToken: String,
        @Query("page") pageNum: Int
    ): Response<APIResponse<BookOnObject>>

    @POST("book")
    suspend fun createBook(
        @Header("Authorization") accessToken: String,
        @Body bookResponse: BookResponse
    ): Response<APIResponse<CreatedBook>>

    @GET("googlebooks")
    suspend fun searchBook(
        @Header("Authorization") accessToken: String,
        @Query("qstring") filter: String,
        @Query("lang") lang: String
    ) : Response<APIResponse<SearchBookObject>>

    @POST("annotation")
    suspend fun saveAnnotation(
        @Header("Authorization") accessToken: String,
        @Body annotation: Annotation,
    ) : Response<APIResponse<AnnotationResponse>>
}