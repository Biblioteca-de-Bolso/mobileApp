package com.bibliotecadebolso.app.ui.add.book

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.BookDataSource
import com.bibliotecadebolso.app.data.model.CreatedBook
import com.bibliotecadebolso.app.data.model.search.BookSearch
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.connectivityScope
import kotlinx.coroutines.launch

class AddBookViewModel : ViewModel() {

    val isBookCreatedResponse = MutableLiveData<Result<CreatedBook>>()
    val booksSearchContent = MutableLiveData<Result<List<BookSearch>>>()

    fun apiCreateBook(accessToken: String,
                      title: String,
                      author: String = "",
                      isbn: String = "",
                      publisher: String = "",
                      description: String = "",
                      thumbnail: String = "") {
        viewModelScope.launch {
            val response = BookDataSource.create(accessToken, title, author, isbn, publisher, description, thumbnail)
            isBookCreatedResponse.postValue(response)
        }


    }

    fun apiListBook(accessToken: String, searchFilter: String) {
        viewModelScope.launch {
            connectivityScope(booksSearchContent) {
                BookDataSource.searchOnline(accessToken, searchFilter)
            }
        }
    }

    fun apiListBookByFilters() {

    }





}