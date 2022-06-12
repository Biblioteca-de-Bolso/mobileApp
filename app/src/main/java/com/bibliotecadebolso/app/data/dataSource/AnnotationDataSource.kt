package com.bibliotecadebolso.app.data.dataSource

import com.bibliotecadebolso.app.data.model.Annotation
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
        page: Int = 0
    ): Result<AnnotationResponse> {
        val result: Result<AnnotationResponse> = RequestUtils.validateErrors {
            val annotation = Annotation(bookId, title, text, page)

            val response = api.saveAnnotation("Bearer $accessToken", annotation)

            val returnedResult = RequestUtils.isResponseSuccessful(response)

            if (returnedResult is Result.Success)
                Result.Success(returnedResult.response)
            else
                returnedResult as Result.Error
        }

        return result
    }


}