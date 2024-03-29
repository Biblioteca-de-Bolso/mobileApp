package com.bibliotecadebolso.app.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.bibliotecadebolso.app.data.model.exceptions.NoInternetException
import com.bibliotecadebolso.app.data.model.response.APIResponse
import com.bibliotecadebolso.app.data.model.response.ErrorResponse
import kotlinx.coroutines.supervisorScope
import retrofit2.Response
import java.net.SocketTimeoutException


object RequestUtils {

    @RequiresApi(Build.VERSION_CODES.M)
    fun deviceIsConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        if (networkCapabilities != null) {
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }

        return false
    }

    fun <T : Any> convertAPIResponseToResultClass(response: Response<APIResponse<T>>): Result<T> {
        return if (response.isSuccessful) {
            resultBasedOnStatusBody(response)
        } else {
            errorResponseTransformed(response)
        }

    }

    fun <T : Any> returnResponseTransformedIntoResult(response: Response<APIResponse<T>>): Result<T> {
        val validationResponse = convertAPIResponseToResultClass(response)

        return if (validationResponse is Result.Success) Result.Success(validationResponse.response)
        else validationResponse as Result.Error
    }

    @JvmName("returnResponseTransformedIntoResult1")
    fun returnResponseTransformedIntoResult(response: Response<APIResponse<Nothing>>): Result<Boolean> {
        val validationResponse = convertAPIResponseToResultClass(response)

        return if (validationResponse is Result.Success) Result.Success(true)
        else validationResponse as Result.Error
    }

    private fun <T : Any> resultBasedOnStatusBody(response: Response<APIResponse<T>>): Result<T> {
        return if (response.body()?.status.equals("ok"))
            Result.Success(response.body()!!.response)
        else
            Result.Error(
                response.code(),
                ErrorResponse(
                    response.body()!!.status!!,
                    response.body()!!.code!!,
                    response.body()!!.message!!
                )
            )
    }

    private fun errorResponseTransformed(response: Response<*>): Result.Error {
        return Result.Error(response.code(), Result.transformToErrorResponse(response.errorBody()))
    }

    suspend fun <T> returnOrThrowIfHasConnectionError(function: suspend () -> Result<T>): Result<T> {
        val result: Result<T> = try {
            function()
        } catch (e: NoInternetException) {
            Result.Error(
                null,
                ErrorResponse("error", "noInternetConnection", e.message)
            )
        }

        return result
    }

    suspend fun <T> connectivityScope(liveData: MutableLiveData<Result<T>>, function: suspend() -> Result<T>) {
        supervisorScope {
            try {
                val result = function()
                liveData.postValue(result)
            } catch (e: SocketTimeoutException) {
                liveData.postValue(returnTooLongRequestResult())
            }

        }
    }

    fun returnTooLongRequestResult(): Result.Error {
        return Result.Error(
            null,
            ErrorResponse(
                "error",
                "tooLongRequest",
                "Internet muito lenta"
            )
        )
    }
}