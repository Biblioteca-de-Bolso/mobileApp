package com.bibliotecadebolso.app.ui.borrow.add

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.request.CreateBorrow
import com.bibliotecadebolso.app.data.validator.ValidationResultUtils
import com.bibliotecadebolso.app.data.validator.validations.ContactNameValidator
import com.bibliotecadebolso.app.databinding.ActivityAddBorrowBinding
import com.bibliotecadebolso.app.ui.book.linearList.BookListActivity
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.SharedPreferencesUtils
import com.bumptech.glide.Glide

/**
 * @param bookId: int,
 *
 */
class AddBorrowActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBorrowBinding
    private lateinit var viewModel: AddBorrowViewModel
    private var bookId: Int = -1
    private var isSelectBookOptionEnabled = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBorrowBinding.inflate(layoutInflater)

        viewModel = ViewModelProvider(this)[AddBorrowViewModel::class.java]

        bookId = intent.extras?.getInt("bookId",-1) ?: -1

        binding.itemBook.apply {
            tvTitle.text = getString(R.string.label_no_book_selected)
            tvAuthor.text = getString(R.string.label_no_book_selected)
        }

        if (bookId != -1) {
            disableBookClickEvent()
            showBookInfo()
        }

        binding.tilContactName.editText?.setText(viewModel.inputs.contactName)

        if (isSelectBookOptionEnabled) {
            binding.btnSelectBorrowBook.visibility = View.VISIBLE
            binding.btnSelectBorrowBook.setOnClickListener {
                getBookByLaunchingBookListActivity()
            }

            binding.itemBook.root.setOnClickListener {
                getBookByLaunchingBookListActivity()
            }
        } else {
            binding.btnSelectBorrowBook.visibility = View.GONE
        }

        viewModel.bookLiveData.observe(this) {
            binding.pgLoading.visibility = View.GONE

            when (it) {
                is Result.Success ->
                    loadBookContent(it.response)
                is Result.Error ->
                    Toast.makeText(this, it.errorBody.message, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.lastSelectedBookId.observe(this) {
            if (it == -1) return@observe
            binding.btnAddLoan.isEnabled = true
        }

        binding.btnAddLoan.setOnClickListener {
            val validationResult = mapOf(
                binding.tilContactName to ContactNameValidator(viewModel.inputs.contactName)
            )

            val hasError = ValidationResultUtils.showErrorOnTextInputLayoutAndReturnIfHasError(
                this,
                validationResult
            )
            if (hasError)
                return@setOnClickListener

            binding.pgLoading.visibility = View.VISIBLE
            val accessToken = SharedPreferencesUtils.getAccessToken(
                getSharedPreferences(
                    Constants.Prefs.USER_TOKENS,
                    MODE_PRIVATE
                )
            )
            viewModel.addBorrow(
                accessToken,
                CreateBorrow(viewModel.lastSelectedBookId.value!!, viewModel.inputs.contactName)
            )


        }

        binding.tilContactName.editText?.addTextChangedListener {
            viewModel.inputs.contactName = it.toString()
        }

        viewModel.createBorrowLiveData.observe(this) {
            binding.pgLoading.visibility = View.GONE

            when (it) {
                is Result.Success ->
                    finish()
                is Result.Error ->
                    Toast.makeText(this, it.errorBody.message, Toast.LENGTH_LONG).show()
            }
        }

        setContentView(binding.root)
    }

    private fun disableBookClickEvent() {
        isSelectBookOptionEnabled = false
    }

    private fun showBookInfo() {
        val accessToken = SharedPreferencesUtils.getAccessToken(
            getSharedPreferences(
                Constants.Prefs.USER_TOKENS,
                MODE_PRIVATE
            )
        )

        viewModel.lastSelectedBookId.postValue(bookId)
        binding.pgLoading.visibility = View.VISIBLE
        viewModel.getBookById(accessToken, bookId)
    }

    private fun getBookByLaunchingBookListActivity() {
        val intent = Intent(this, BookListActivity::class.java)
        intent.putExtra("toReturnEnum", BookListActivity.Companion.TO_RETURN.BOOKID)
        intent.putExtra("showBorrowStatus", true)
        intent.putExtra("cancelClickIfBorrowed", true)

        bookListActivityResult.launch(intent)
    }

    private fun loadBookContent(book: Book) {
        binding.itemBook.apply {

            val defaultThumbnailInt = R.drawable.ic_item_book

            tvTitle.text = book.title
            tvAuthor.text = book.author
            tvIsbn10.text = "ISBN-10: ${book.isbn10}"
            tvIsbn13.text = "ISBN-13: ${book.isbn13}"

            Glide.with(this@AddBorrowActivity)
                .load(book.thumbnail.ifEmpty { defaultThumbnailInt })
                .centerInside()
                .into(ivThumbnail)
        }
    }


    private val bookListActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == BookListActivity.RETURN_BOOK_ID) {
            val accessToken = SharedPreferencesUtils.getAccessToken(
                getSharedPreferences(
                    Constants.Prefs.USER_TOKENS,
                    MODE_PRIVATE
                )
            )
            val bookId = result.data?.extras?.getInt("bookId")
            if (bookId != null) {
                viewModel.lastSelectedBookId.postValue(bookId)
                binding.pgLoading.visibility = View.VISIBLE
                viewModel.getBookById(accessToken, bookId)
            }
        }
    }
}