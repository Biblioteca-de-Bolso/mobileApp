package com.bibliotecadebolso.app.ui.add.annotation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.AnnotationDataSource
import com.bibliotecadebolso.app.data.model.Annotation
import com.bibliotecadebolso.app.data.model.AnnotationObject
import com.bibliotecadebolso.app.data.model.enum.TransactionOptions
import com.bibliotecadebolso.app.data.model.response.AnnotationResponse
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.launch

class AnnotationEditorViewModel : ViewModel() {

    val transactionOptionSelected = MutableLiveData<TransactionOptions>()
    val resultOfSaveAnnotation = MutableLiveData<Result<AnnotationResponse?>>()
    val updateAnnotationResult = MutableLiveData<Result<AnnotationObject>>()
    val getByIdResult = MutableLiveData<Result<AnnotationObject>>()
    var getByIdAlreadyLoaded = false;


    var richEditorHtmlData = ""
        private set
    var richEditorHtmlDataChanged = false
        private set

    var referenceText = ""
        private set
    var referenceChanged = false
        private set

    var titleText = ""
        private set
    var titleChanged = false
        private set

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
            getByIdAlreadyLoaded = true
        }
    }

    fun changeHtmlBody(newHtmlBody: String) {
        richEditorHtmlData = newHtmlBody
        richEditorHtmlDataChanged = true
    }

    fun changeTitle(newTitle: String) {
        titleText = newTitle
        titleChanged = true
    }

    fun changeReference(newReference: String) {
        referenceText = newReference
        referenceChanged = true
    }

    fun loadAnnotationContent(annotation: Annotation) {
        richEditorHtmlData = annotation.text
        titleText = annotation.title
        referenceText = annotation.reference
    }

}