package com.bibliotecadebolso.app.ui.bookInfo.annotationList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.AnnotationDataSource
import com.bibliotecadebolso.app.data.model.ListAnnotationObject
import com.bibliotecadebolso.app.util.Result
import kotlinx.coroutines.launch

class AnnotationListViewModel : ViewModel() {

    val liveDataAnnotationList = MutableLiveData<Result<ListAnnotationObject>>()

    fun getList(accessToken: String, bookId: Int, page: Int) {
        viewModelScope.launch {
            val result = AnnotationDataSource.getlist(accessToken, bookId, page)
            liveDataAnnotationList.postValue(result)
        }
    }
}