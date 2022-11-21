package com.bibliotecadebolso.app.util

import androidx.lifecycle.MutableLiveData

suspend fun <T> connectivityScope(liveData: MutableLiveData<Result<T>>, function: suspend()-> Result<T>)
= RequestUtils.connectivityScope(liveData, function)