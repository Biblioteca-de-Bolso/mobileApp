package com.bibliotecadebolso.app.ui.bookInfo

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.loader.content.CursorLoader
import com.bibliotecadebolso.app.data.dataSource.BookDataSource
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.UpdateBook
import com.bibliotecadebolso.app.data.model.UpdatedBook
import com.bibliotecadebolso.app.util.Result
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import kotlinx.coroutines.launch
import java.io.File


class BookInfoViewModel : ViewModel() {

    val liveDataBookInfo = MutableLiveData<Result<Book>>()
    var isDescriptionShowMoreActive = false
    val liveDataDeleteBook = MutableLiveData<Result<String>>()
    val liveDataUpdateBook = MutableLiveData<Result<UpdatedBook>>()
    val dataSource = BookDataSource
    val liveDataImageCompressed = MutableLiveData<File>()
    val liveDataUpdateImage = MutableLiveData<Result<String>>()

    var isToShowConfirmationDisplay = false
        private set

    fun setDisplayStatusConfirmation(boolean: Boolean) {
        Log.e("onViewModel", boolean.toString())
        isToShowConfirmationDisplay = boolean
    }


    fun getInfoByID(accessToken: String, id: Int) {
        viewModelScope.launch {
            val result = dataSource.getBookById(accessToken, id)
            liveDataBookInfo.postValue(result)
        }
    }

    fun deleteBook(accessToken: String, bookId: Int) {
        viewModelScope.launch {
            val result = dataSource.deleteBookById(accessToken, bookId)
            liveDataDeleteBook.postValue(result)
        }
    }

    fun updateBook(accessToken: String, book: UpdateBook) {
        viewModelScope.launch {
            val result = dataSource.updateBookById(accessToken, book)

            liveDataUpdateBook.postValue(result)
        }
    }

    fun compressImage(context: Context, imagePath: String) {
        viewModelScope.launch {
            val compressedFile = Compressor.compress(context, File(imagePath)) {
                format(Bitmap.CompressFormat.WEBP)
            }

            Log.e("BookInfoViewModel", "Image compressed")
            liveDataImageCompressed.postValue(compressedFile)

        }
    }

    fun updateImageBookById(
        context: Context,
        accessToken: String,
        bookId: Int,
        file: File
    ) {
        viewModelScope.launch {
            liveDataImageCompressed.value?.let {
                val response = BookDataSource.updateImageBookById(context, accessToken, bookId,
                    it
                )

                liveDataUpdateImage.postValue(response)
            }
        }
    }

}