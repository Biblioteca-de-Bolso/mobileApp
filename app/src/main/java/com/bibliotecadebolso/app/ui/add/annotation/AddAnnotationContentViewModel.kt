package com.bibliotecadebolso.app.ui.add.annotation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.AnnotationDataSource
import com.bibliotecadebolso.app.data.dataSource.BookDataSource
import com.bibliotecadebolso.app.data.model.AnnotationObject
import com.bibliotecadebolso.app.data.model.enum.TransactionOptions
import com.bibliotecadebolso.app.data.model.response.AnnotationResponse
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.launch

class AddAnnotationContentViewModel : ViewModel() {

    val transactionOptionSelected = MutableLiveData<TransactionOptions>()
    val resultOfSaveAnnotation = MutableLiveData<Result<AnnotationResponse?>>()
    val updateAnnotationResult = MutableLiveData<Result<AnnotationObject>>()
    val getByIdResult = MutableLiveData<Result<AnnotationObject>>()

    var richEditorHtmlData = ""

    fun saveAnnotation(accessToken: String, bookId: Int, title: String, text: String, reference: String = "") {
        viewModelScope.launch {
            val result = AnnotationDataSource.saveAnnotation(accessToken, bookId, title, text, reference)
            resultOfSaveAnnotation.postValue(result)
        }

    }

    fun updateAnnotation(accessToken: String, annotationId: Int, title: String, text: String, reference: String = "") {
        viewModelScope.launch {
            val result = AnnotationDataSource.updateAnnotation(accessToken, annotationId, title, text, reference)
            updateAnnotationResult.postValue(result)
        }
    }

    fun getAnnotationById(accessToken: String, annotationId: Int) {
        viewModelScope.launch {
            val result = AnnotationDataSource.getById(accessToken, annotationId)
            getByIdResult.postValue(result)
        }
    }
}