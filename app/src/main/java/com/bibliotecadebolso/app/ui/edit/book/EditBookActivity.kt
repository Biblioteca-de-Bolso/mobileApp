package com.bibliotecadebolso.app.ui.edit.book

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.UpdateBook
import com.bibliotecadebolso.app.data.validator.BookValidator
import com.bibliotecadebolso.app.databinding.ActivityEditBookBinding
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.SharedPreferencesUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

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
        viewModel.getBookById(accessToken, bookId)


        viewModel.bookLiveData.observe(this) {
            if (it is Result.Success) {
                fillInputWithData(it.response)
            }
        }

        setupOnClickEditBook()
        updateBookListener()
    }

    private fun fillInputWithData(book: Book) {
        binding.apply {
            etBookTitle.editText?.setText(book.title)
            etBookAuthor.editText?.setText(book.author)
            etBookIsbn10Or13.editText?.setText(book.ISBN_13)
            etBookDescription.editText?.setText(book.description)
            etBookPublisher.editText?.setText(book.publisher)
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
                binding.progressSending.visibility = View.VISIBLE
                updateBook()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val tilTitle = binding.etBookTitle
        val tilAuthor = binding.etBookAuthor
        val tilPublisher = binding.etBookPublisher
        val tilDescription = binding.etBookDescription
        val bookValidator = BookValidator

        if (!bookValidator.isTitleValid(tilTitle.editText!!.text.toString())) {
            tilTitle.error = getString(R.string.error_must_be_beetween_1_128)
            return false
        }

        if (!bookValidator.isAuthorNameValid(tilAuthor.editText!!.text.toString())) {
            tilAuthor.error = getString(R.string.error_must_be_between_0_128)
            return false
        }

        if (!bookValidator.isPublisherNameValid(tilPublisher.editText!!.text.toString())) {
            tilPublisher.error = getString(R.string.error_must_be_between_0_128)
            return false
        }


        return true
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
            updateBook = UpdateBook(bookId, title, author, isbn, publisher, description, viewModel.lastReadingStatus)
        )
    }

    private fun updateBookListener() {
        viewModel.updatedBookLiveData.observe(this) {
            binding.progressSending.visibility = View.INVISIBLE
            if (it is Result.Success) {
                Toast.makeText(
                    this,
                    getString(R.string.label_book_updated_sucessfully),
                    Toast.LENGTH_LONG
                ).show()

                finish()
            }
        }
    }
}