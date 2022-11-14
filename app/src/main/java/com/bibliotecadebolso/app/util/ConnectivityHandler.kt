package com.bibliotecadebolso.app.util

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler

object ConnectivityHandler {

    val handler = CoroutineExceptionHandler { _, exception ->
        Log.wtf("Network", "Caught $exception")
    }
}