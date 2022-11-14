package com.bibliotecadebolso.app.ui.home.ui.bookList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.BookDataSource
import com.bibliotecadebolso.app.data.model.CreatedBook
import com.bibliotecadebolso.app.data.model.ReadStatusEnum
import com.bibliotecadebolso.app.data.model.response.ErrorResponse
import com.bibliotecadebolso.app.data.validator.ValidationResultUtils
import com.bibliotecadebolso.app.ui.ResultCodes
import com.bibliotecadebolso.app.util.ConnectivityHandler
import com.bibliotecadebolso.app.util.RequestUtils
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.net.SocketTimeoutException

class HomeViewModel : ViewModel() {


    val bookListPlanning = MutableLiveData<Result<List<CreatedBook>>>()
    val bookListReading = MutableLiveData<Result<List<CreatedBook>>>()
    val bookListDropped = MutableLiveData<Result<List<CreatedBook>>>()
    val bookListConcluded = MutableLiveData<Result<List<CreatedBook>>>()


    fun apiListBook(accessToken: String, pageNum: Int = 1, readStatusEnum: ReadStatusEnum? = null) {
        viewModelScope.launch {
            supervisorScope {
                try {
                    val response = BookDataSource.list(accessToken, pageNum, readStatusEnum)
                    applyResponseToLiveData(response, readStatusEnum)
                } catch (e: SocketTimeoutException) {
                    applyResponseToLiveData(
                        RequestUtils.returnTooLongRequestResult(), readStatusEnum
                    )
                }

            }

        }
    }

    private fun applyResponseToLiveData(
        response: Result<List<CreatedBook>>,
        readStatusEnum: ReadStatusEnum?
    ) {
        readStatusEnum?.let {
            when (readStatusEnum) {
                ReadStatusEnum.PLANNING -> bookListPlanning.postValue(response)
                ReadStatusEnum.READING -> bookListReading.postValue(response)
                ReadStatusEnum.DROPPED -> bookListDropped.postValue(response)
                ReadStatusEnum.CONCLUDED -> bookListConcluded.postValue(response)
            }
        }
    }
}