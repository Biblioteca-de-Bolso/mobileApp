package com.bibliotecadebolso.app.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.bibliotecadebolso.app.data.model.exceptions.NoInternetException
import com.bibliotecadebolso.app.data.model.response.APIResponse
import com.bibliotecadebolso.app.data.model.response.ErrorResponse
import retrofit2.Response


object RequestUtils {

    @RequiresApi(Build.VERSION_CODES.M)
    fun deviceIsConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        if (networkCapabilities != null) {
            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->  true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }

        return false
    }

    fun <T: Any> isResponseSuccessful(response: Response<APIResponse<T>>): Result<T> {
        return if (response.isSuccessful) {
            resultBasedOnStatusBody(response)
        } else {
            errorResponseTransformed(response)
        }

    }

    private fun <T: Any> resultBasedOnStatusBody(response: Response<APIResponse<T>>): Result<T> {
        return if (response.body()?.status.equals("ok"))
            Result.Success(response.body()!!.response)
        else
            errorResponseTransformed(response)
    }

    private fun errorResponseTransformed(response: Response<*>): Result.Error {
        return Result.Error(response.code(), Result.transformToErrorResponse(response.errorBody()))
    }

    suspend fun <T> validateErrors(function: suspend () -> Result<T>): Result<T> {
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
}