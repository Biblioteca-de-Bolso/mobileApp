package com.bibliotecadebolso.app.ui.bookInfo

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.dataSource.BookDataSource
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.ReadStatusEnum
import com.bibliotecadebolso.app.data.model.UpdateBook
import com.bibliotecadebolso.app.data.model.UpdatedBook
import com.bibliotecadebolso.app.util.Result
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File
import java.text.DecimalFormat
import kotlin.math.pow


class BookInfoViewModel : ViewModel() {

    val liveDataBookInfo = MutableLiveData<Result<Book>>()
    var isDescriptionShowMoreActive = false
    val liveDataDeleteBook = MutableLiveData<Result<String>>()
    val liveDataUpdateBook = MutableLiveData<Result<UpdatedBook>>()
    val dataSource = BookDataSource
    val liveDataImageCompressed = MutableLiveData<File>()
    val liveDataUpdateImage = MutableLiveData<Result<String>>()
    val readingStatusValuesKey = HashMap<String, String>()
    var isToShowConfirmationDisplay = false
        private set

    fun setDisplayStatusConfirmation(boolean: Boolean) {
        Log.e("onViewModel", boolean.toString())
        isToShowConfirmationDisplay = boolean
    }

    fun setReadStatusValuesKeyMap(
        spinnerReadingStatusKey: Array<String>,
        spinnerReadingStatusValue: Array<String>
    ) {
        readingStatusValuesKey.clear()
        for (i in spinnerReadingStatusKey.indices) {
            readingStatusValuesKey[spinnerReadingStatusValue[i]] = spinnerReadingStatusKey[i]
        }
    }

    fun getReadingStatusKeyByIndex(indexOfReadingStatusLabel: Int) =
        readingStatusValuesKey.keys.toList()[indexOfReadingStatusLabel]

    fun getReadStatusEnumIndexOnSpinner(readStatusEnum: ReadStatusEnum) =
        readingStatusValuesKey.values.toList()
            .indexOf(readStatusEnum.toString())


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

    fun updateBookByPatch(accessToken: String, book: UpdateBook) {
        viewModelScope.launch {
            val result = dataSource.updateBookByIdWithPatch(accessToken, book)
            liveDataUpdateBook.postValue(result)
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
            val file = File(imagePath)
            Log.e("compressedImageSize", getReadableFileSize(file.length()))
            val compressedFile = Compressor.compress(context, file) {
                format(Bitmap.CompressFormat.WEBP)
                size(400_000)
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
            val response = BookDataSource.updateImageBookById(context, accessToken, bookId, file)
            liveDataUpdateImage.postValue(response)
        }
    }

    fun getReadableFileSize(size: Long): String {
        if (size <= 0) {
            return "0"
        }
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }

    fun getDescription(description: StringBuilder): String {
        return if (isDescriptionShowMoreActive)
            description.toString()
        else {
            if (isAShortDescription(description)) {
                description.toString()
            } else
                setShortDescription(description)
        }
    }

    fun isAShortDescription(description: StringBuilder) = description.length <= 270

    private fun setShortDescription(description: StringBuilder) =
        description.substring(0, 270) + "..."

}