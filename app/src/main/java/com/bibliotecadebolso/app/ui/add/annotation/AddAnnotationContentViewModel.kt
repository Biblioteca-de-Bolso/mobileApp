package com.bibliotecadebolso.app.ui.add.annotation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.AnnotationDataSource
import com.bibliotecadebolso.app.data.dataSource.BookDataSource
import com.bibliotecadebolso.app.data.model.enum.TransactionOptions
import com.bibliotecadebolso.app.data.model.response.AnnotationResponse
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.launch

class AddAnnotationContentViewModel : ViewModel() {

    val transactionOptionSelected = MutableLiveData<TransactionOptions>()
    val resultOfSaveAnnotation = MutableLiveData<Result<AnnotationResponse?>>()

    fun saveAnnotation(accessToken: String, bookId: Int, title: String, text: String, reference: String = "") {
        viewModelScope.launch {
            val result = AnnotationDataSource.saveAnnotation(accessToken, bookId, title, text, reference)
            resultOfSaveAnnotation.postValue(result)
        }

    }
}