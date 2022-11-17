package com.bibliotecadebolso.app.data.dataSource

import com.bibliotecadebolso.app.data.model.*
import com.bibliotecadebolso.app.data.model.Annotation
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
    ): Result<AnnotationObject> {
        val result: Result<AnnotationObject> = RequestUtils.returnOrThrowIfHasConnectionError {
            val annotation = SaveAnnotation(bookId, title, text, reference)
            val response = api.saveAnnotation("Bearer $accessToken", annotation)
            val returnedResult = RequestUtils.convertAPIResponseToResultClass(response)
            if (returnedResult is Result.Success)
                Result.Success(returnedResult.response)
            else
                returnedResult as Result.Error
        }

        return result
    }

    suspend fun getListObject(
        accessToken: String,
        bookId: Int,
        page: Int,
        searchContent: String? = null
    ): Result<ListAnnotationObject> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.getAnnotationList("Bearer $accessToken", page, bookId, searchContent)
            val responseResult = RequestUtils.convertAPIResponseToResultClass(response)
            if (responseResult is Result.Success)
                Result.Success(responseResult.response)
            else
                responseResult as Result.Error
        }

    }

    suspend fun getList(
        accessToken: String,
        bookId: Int? = null,
        page: Int,
        searchContent: String? = null
    ): Result<List<Annotation>> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.getAnnotationList("Bearer $accessToken", page, bookId, searchContent)
            val responseResult = RequestUtils.convertAPIResponseToResultClass(response)
            if (responseResult is Result.Success)
                Result.Success(responseResult.response.annotations)
            else
                responseResult as Result.Error
        }

    }

    suspend fun getById(accessToken: String, annotationId: Int): Result<AnnotationObject> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.getAnnotationById("Bearer $accessToken", annotationId)
            val responseResult = RequestUtils.convertAPIResponseToResultClass(response)
            if (responseResult is Result.Success)
                Result.Success(responseResult.response)
            else
                responseResult as Result.Error
        }
    }

    suspend fun updateAnnotation(
        accessToken: String,
        annotationId: Int,
        title: String,
        text: String,
        reference: String
    ): Result<AnnotationObject> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val updateAnnotation = UpdateAnnotation(annotationId, title, text, reference)
            val response = api.updateAnnotation("Bearer $accessToken", updateAnnotation)
            val responseResult = RequestUtils.convertAPIResponseToResultClass(response)
            if (responseResult is Result.Success)
                Result.Success(responseResult.response)
            else
                responseResult as Result.Error
        }
    }

    suspend fun deleteAnnotation(
        accessToken: String,
        annotationId: Int,
    ): Result<Boolean> {
        return RequestUtils.returnOrThrowIfHasConnectionError {
            val response = api.deleteAnnotationById("Bearer $accessToken", id = annotationId)
            RequestUtils.returnResponseTransformedIntoResult(response)
        }
    }


}