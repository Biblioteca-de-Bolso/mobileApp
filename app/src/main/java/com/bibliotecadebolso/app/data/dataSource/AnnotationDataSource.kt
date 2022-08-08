package com.bibliotecadebolso.app.data.dataSource

import com.bibliotecadebolso.app.data.model.AnnotationObject
import com.bibliotecadebolso.app.data.model.BookIdObject
import com.bibliotecadebolso.app.data.model.SaveAnnotation
import com.bibliotecadebolso.app.data.model.response.AnnotationResponse
import com.bibliotecadebolso.app.data.repository.BibliotecaDeBolsoRepository
import com.bibliotecadebolso.app.util.RequestUtils
import com.bibliotecadebolso.app.util.Result

object AnnotationDataSource {

    private val api = BibliotecaDeBolsoRepository.retrofit()

    suspend fun saveAnnotation(
        accessToken: String,
        bookId: Int,
        title: String,
        text: String,
        reference: String
    ): Result<AnnotationResponse> {
        val result: Result<AnnotationResponse> = RequestUtils.returnOrThrowIfHasConnectionError {
            val annotation = SaveAnnotation(bookId, title, text, reference)
            val response = api.saveAnnotation("Bearer $accessToken", annotation)
            val returnedResult = RequestUtils.isResponseSuccessful(response)
            if (returnedResult is Result.Success)
                Result.Success(returnedResult.response)
            else
                returnedResult as Result.Error
        }

        return result
    }

    suspend fun getlist(accessToken: String, bookId: Int, page: Int): Result<AnnotationObject> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.getAnnotationList("Bearer $accessToken", page, bookId)
            val responseResult = RequestUtils.isResponseSuccessful(response)
            if (responseResult is Result.Success)
                Result.Success(responseResult.response)
            else
                responseResult as Result.Error
        }

    }


}