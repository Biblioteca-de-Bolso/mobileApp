package com.bibliotecadebolso.app.data.api

import com.bibliotecadebolso.app.data.model.*
import com.bibliotecadebolso.app.data.model.SaveAnnotation
import com.bibliotecadebolso.app.data.model.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        @Header("Authorization") accessToken: String,
        @Body refreshToken: RefreshTokenObject,
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
        @Query("page") pageNum: Int,
        @Query("readStatus") readStatusEnum: ReadStatusEnum?,
        @Query("search") searchContent: String? = null
    ): Response<APIResponse<BookListInObject>>

    @GET("book/{id}")
    suspend fun getBookById(
        @Header("Authorization") accessToken: String,
        @Path("id") id: Int
    ): Response<APIResponse<BookInObject>>

    @HTTP(method = "DELETE", path = "book", hasBody = true)
    suspend fun deleteBookById(
        @Header("Authorization") accessToken: String,
        @Body bookId: BookIdObject
    ): Response<APIResponse<Nothing>>

    @POST("book")
    suspend fun createBook(
        @Header("Authorization") accessToken: String,
        @Body bookResponse: BookResponse
    ): Response<APIResponse<CreatedBook>>

    @PUT("book")
    suspend fun updateBookById(
        @Header("Authorization") accessToken: String,
        @Body book: UpdateBook
    ): Response<APIResponse<UpdatedBookObject>>

    @PATCH("book")
    suspend fun updateBookByIdWithPatch(
        @Header("Authorization") accessToken: String,
        @Body book: UpdateBook
    ): Response<APIResponse<UpdatedBookObject>>

    @Multipart
    @PUT("book/thumbnail")
    suspend fun updateBookImageById(
        @Header("Authorization") accessToken: String,
        @Part("bookId") bookId: RequestBody,
        @Part thumbnailFile: MultipartBody.Part
    ): Response<APIResponse<Any>>

    @GET("googlebooks")
    suspend fun searchBook(
        @Header("Authorization") accessToken: String,
        @Query("qstring") filter: String,
        @Query("lang") lang: String
    ): Response<APIResponse<SearchBookObject>>

    @POST("annotation")
    suspend fun saveAnnotation(
        @Header("Authorization") accessToken: String,
        @Body annotation: SaveAnnotation,
    ): Response<APIResponse<AnnotationResponse>>

    @PUT("annotation")
    suspend fun updateAnnotation(
        @Header("Authorization") accessToken: String,
        @Body annotation: UpdateAnnotation
    ): Response<APIResponse<AnnotationObject>>

    @GET("annotation")
    suspend fun getAnnotationList(
        @Header("Authorization") accessToken: String,
        @Query("page") page: Int = 1,
        @Query("bookId") bookId: Int,
    ): Response<APIResponse<ListAnnotationObject>>

    @GET("annotation/{id}")
    suspend fun getAnnotationById(
        @Header("Authorization") accessToken: String,
        @Path("id") id: Int
    ): Response<APIResponse<AnnotationObject>>
}