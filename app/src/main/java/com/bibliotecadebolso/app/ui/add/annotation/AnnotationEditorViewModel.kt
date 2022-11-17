package com.bibliotecadebolso.app.ui.add.annotation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.AnnotationDataSource
import com.bibliotecadebolso.app.data.model.Annotation
import com.bibliotecadebolso.app.data.model.AnnotationObject
import com.bibliotecadebolso.app.data.model.enum.TransactionOptions
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.connectivityScope
import kotlinx.coroutines.launch

class AnnotationEditorViewModel : ViewModel() {

    val transactionOptionSelected = MutableLiveData<TransactionOptions>()
    val resultOfSaveAnnotation = MutableLiveData<Result<AnnotationObject>>()
    var annotationSaved = false

    val updateAnnotationResult = MutableLiveData<Result<AnnotationObject>>()
    var annotationUpdated = false
    val getByIdResult = MutableLiveData<Result<AnnotationObject>>()
    val deleteAnnotationResult = MutableLiveData<Result<Boolean>>()
    var annotationDeleted = false

    val exportToPdfLiveData = MutableLiveData<Result<Any>>()
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


    fun saveAnnotation(
        accessToken: String,
        bookId: Int,
        title: String,
        text: String,
        reference: String = ""
    ) {
        viewModelScope.launch {
            connectivityScope(resultOfSaveAnnotation) {
                val result = AnnotationDataSource.saveAnnotation(accessToken, bookId, title, text, reference)
                if (result is Result.Success) annotationSaved = true
                result
            }
        }

    }

    fun updateAnnotation(
        accessToken: String,
        annotationId: Int,
        title: String,
        text: String,
        reference: String = ""
    ) {
        viewModelScope.launch {
            connectivityScope(updateAnnotationResult) {
                val result = AnnotationDataSource.updateAnnotation(
                    accessToken,
                    annotationId,
                    title,
                    text,
                    reference
                )

                if (result is Result.Success) annotationUpdated = true
                result
            }
        }
    }

    fun getAnnotationById(accessToken: String, annotationId: Int) {
        viewModelScope.launch {
            connectivityScope(getByIdResult) {
                AnnotationDataSource.getById(accessToken, annotationId)
            }
            getByIdAlreadyLoaded = true

        }
    }

    fun deleteAnnotation(accessToken: String, annotationId: Int) {
        viewModelScope.launch {
            connectivityScope(deleteAnnotationResult) {
                val result = AnnotationDataSource.deleteAnnotation(accessToken, annotationId)

                if (result is Result.Success) annotationDeleted = true
                result
            }
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