package com.bibliotecadebolso.app.ui.book.bookInfo

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibliotecadebolso.app.data.dataSource.BookDataSource
import com.bibliotecadebolso.app.data.dataSource.BorrowDataSource
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.ReadStatusEnum
import com.bibliotecadebolso.app.data.model.UpdateBook
import com.bibliotecadebolso.app.data.model.UpdatedBook
import com.bibliotecadebolso.app.data.model.request.Borrow
import com.bibliotecadebolso.app.data.model.request.BorrowStatus
import com.bibliotecadebolso.app.util.RequestUtils
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.connectivityScope
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.io.File
import java.net.SocketTimeoutException


class BookInfoViewModel : ViewModel() {

    class BookResponse {
        val generalLiveDataInfo = MutableLiveData<Result<Book>>()
        val deleteLiveData = MutableLiveData<Result<String>>()
        val updateStatusLiveData = MutableLiveData<Result<UpdatedBook>>()
        val liveDataUpdateImage = MutableLiveData<Result<String>>()
    }

    class State {
        var isDescriptionShowMoreActive = false
        var updatedStatus = false
        var updatedBookImage = false
        var updatedBookInfo = false
    }

    val bookResponses = BookResponse()
    val states = State()
    val liveDataPendingBorrowList = MutableLiveData<Result<List<Borrow>>>()
    val liveDataImageCompressed = MutableLiveData<File>()

    val dataSource = BookDataSource
    val borrowDataSource = BorrowDataSource()
    val readingStatusValuesKey = HashMap<String, String>()

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
            connectivityScope(bookResponses.generalLiveDataInfo) {
                dataSource.getBookById(accessToken, id)
            }
        }

    }

    fun deleteBook(accessToken: String, bookId: Int) {
        viewModelScope.launch {
            connectivityScope(bookResponses.deleteLiveData) {
                dataSource.deleteBookById(accessToken, bookId)
            }
        }
    }

    fun updateBookByPatch(accessToken: String, book: UpdateBook) {
        viewModelScope.launch {
            val result = dataSource.updateBookByIdWithPatch(accessToken, book)
            bookResponses.updateStatusLiveData.postValue(result)
        }
    }

    fun getLastReadStatusEnumOrNull(): ReadStatusEnum? {
        val lastUpdatedValue = bookResponses.updateStatusLiveData.value

        return if (lastUpdatedValue != null && lastUpdatedValue is Result.Success)
            (lastUpdatedValue).response.readStatus
        else
            (bookResponses.generalLiveDataInfo.value as Result.Success).response.readStatus
    }

    fun checkIfCanBorrowBook(accessToken: String, bookId: Int) {
        viewModelScope.launch {
            val result = borrowDataSource.listBorrow(
                accessToken,
                1,
                bookId = bookId,
                borrowStatus = BorrowStatus.PENDING
            )

            liveDataPendingBorrowList.postValue(result)

        }
    }

    fun compressImage(context: Context, imagePath: String) {
        viewModelScope.launch {
            val file = File(imagePath)
            val compressedFile = Compressor.compress(context, file) {
                format(Bitmap.CompressFormat.WEBP)
                size(400_000)
            }

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
            connectivityScope(bookResponses.liveDataUpdateImage) {
                BookDataSource.updateImageBookById(context, accessToken, bookId, file)
            }
        }
    }

    fun getDescription(description: StringBuilder): String {
        return if (states.isDescriptionShowMoreActive)
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