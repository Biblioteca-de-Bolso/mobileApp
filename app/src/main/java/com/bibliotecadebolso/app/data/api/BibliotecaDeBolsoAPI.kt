package com.bibliotecadebolso.app.data.api

import com.bibliotecadebolso.app.data.model.*
import com.bibliotecadebolso.app.data.model.Annotation
import com.bibliotecadebolso.app.data.model.response.*
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

    @POST("auth/refresh")
    suspend fun getNewAccessToken(
        @Body refreshToken: String,
    ): Response<APIResponse<AuthTokens>>

    @FormUrlEncoded
    @POST("user")
    suspend fun register(
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("password") password: String,
    ): Response<APIResponse<UserObject>>


    @HTTP(method = "DELETE", path = "user", hasBody = true)
    suspend fun delete(
        @Header("Authorization") accessToken: String,
        @Body deleteForm: DeleteForm
    ): Response<APIResponse<DeleteAccountResponse>>


    @GET("book")
    suspend fun bookList(
        @Header("Authorization") accessToken: String,
        @Query("page") pageNum: Int
    ): Response<APIResponse<BookListInObject>>

    @GET("book/{id}")
    suspend fun getBookById(
        @Header("Authorization") accessToken: String,
        @Path("id") id: Int
    ): Response<APIResponse<BookInObject>>

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
    ): Response<APIResponse<SearchBookObject>>

    @POST("annotation")
    suspend fun saveAnnotation(
        @Header("Authorization") accessToken: String,
        @Body annotation: Annotation,
    ): Response<APIResponse<AnnotationResponse>>
}