package com.bibliotecadebolso.app.ui.book.edit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.UpdateBook
import com.bibliotecadebolso.app.data.model.response.BookResponse
import com.bibliotecadebolso.app.data.validator.Validation
import com.bibliotecadebolso.app.data.validator.ValidationError
import com.bibliotecadebolso.app.data.validator.validations.BookValidation
import com.bibliotecadebolso.app.databinding.ActivityEditBookBinding
import com.bibliotecadebolso.app.ui.ResultCodes
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.SharedPreferencesUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar

/**
 *
 * @param bookId required
 */
class EditBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBookBinding
    private lateinit var viewModel: EditBookViewModel
    private var bookId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[EditBookViewModel::class.java]

        bookId = intent.extras?.getLong("bookId") ?: -1L

        if (bookId == -1L) {
            Toast.makeText(this, getString(R.string.label_book_id_not_valid), Toast.LENGTH_LONG)
                .show()
            finish()
        }

        val accessToken = SharedPreferencesUtils.getAccessToken(
            getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)
        )
        showLoadingBar()
        viewModel.getBookById(accessToken, bookId)


        viewModel.bookLiveData.observe(this) {
            hideLoadingBar()
            if (it is Result.Success) {
                fillInputWithData(it.response)
            } else if (it is Result.Error) {
                Toast.makeText(this, it.errorBody.message, Toast.LENGTH_LONG).show()
            }
        }

        setupOnClickEditBook()
        updateBookListener()
    }

    private fun fillInputWithData(book: Book) {
        binding.apply {
            etBookTitle.editText?.setText(book.title)
            etBookAuthor.editText?.setText(book.author)
            etBookIsbn10Or13.editText?.setText(
                book.isbn13.ifEmpty { book.isbn10 })
            etBookDescription.editText?.setText(book.description)
            etBookPublisher.editText?.setText(book.publisher)
            viewModel.bookThumbnail = book.thumbnail
            if (book.thumbnail.isNotEmpty())
                Glide.with(this@EditBookActivity).load(book.thumbnail)
                    .centerCrop()
                    .apply(RequestOptions().override(400, 600))
                    .into(ivBookPreview)
        }
    }

    private fun setupOnClickEditBook() {
        val btnEditBook = binding.btnEditBook

        btnEditBook.setOnClickListener {
            val isValid: Boolean = validateInputs()
            if (isValid) {
                showLoadingBar()
                updateBook()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val title = binding.etBookTitle.editText!!.text.toString()
        val author = binding.etBookAuthor.editText!!.text.toString()
        val publisher = binding.etBookPublisher.editText!!.text.toString()
        val description = binding.etBookDescription.editText!!.text.toString()
        val isbn = binding.etBookIsbn10Or13.editText!!.text.toString()

        val updateBook = BookResponse(title, author, isbn, publisher, description)
        val validation = Validation(listOf(BookValidation(updateBook)))

        val errors = validation.checkAllValidations()
        if (errors.isEmpty()) {
            cleanInputErrors()
            return true
        }

        showInputErrors(errors)
        return false
    }

    private fun cleanInputErrors() {
        binding.apply {
            etBookTitle.error = ""
            etBookAuthor.error = ""
            etBookPublisher.error = ""
        }
    }

    private fun showInputErrors(errors: List<ValidationError>) {
        binding.apply {
            errors.forEach {
                when (it.field) {
                    "title" ->
                        etBookTitle.error = getString(R.string.error_must_be_beetween_1_128)
                    "author" ->
                        etBookAuthor.error = getString(R.string.error_must_be_between_0_128)
                    "publisher" ->
                        etBookPublisher.error = getString(R.string.error_must_be_between_0_128)
                    else ->
                        Snackbar.make(binding.root, it.errorMessage, Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun updateBook() {
        val title = binding.etBookTitle.editText!!.text.toString()
        val author = binding.etBookAuthor.editText!!.text.toString()
        val publisher = binding.etBookPublisher.editText!!.text.toString()
        val description = binding.etBookDescription.editText!!.text.toString()
        val isbn = binding.etBookIsbn10Or13.editText!!.text.toString()

        val accessToken = SharedPreferencesUtils.getAccessToken(
            getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)
        )
        viewModel.updateBook(
            accessToken = accessToken,
            updateBook = UpdateBook(
                bookId,
                title,
                author,
                isbn,
                publisher,
                viewModel.bookThumbnail,
                description,
                viewModel.lastReadingStatus
            )
        )
    }

    private fun updateBookListener() {
        viewModel.updatedBookLiveData.observe(this) {
            hideLoadingBar()
            if (it is Result.Success) {
                Toast.makeText(
                    this,
                    getString(R.string.label_book_updated_sucessfully),
                    Toast.LENGTH_LONG
                ).show()

                val returnResult = Intent()
                intent.putExtra("id", bookId)
                setResult(ResultCodes.BOOK_EDITED, returnResult)
                finish()
            } else if (it is Result.Error) {
                Snackbar.make(binding.root, it.errorBody.message, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun hideLoadingBar() {
        binding.progressSending.visibility = View.GONE
    }

    private fun showLoadingBar() {
        binding.progressSending.visibility = View.VISIBLE
    }
}