package com.bibliotecadebolso.app.ui.add.book

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.search.BookSearch
import com.bibliotecadebolso.app.data.validator.BookValidator
import com.bibliotecadebolso.app.databinding.FragmentAddBookOfflineInputBinding
import com.bibliotecadebolso.app.ui.home.ui.bookList.BookListFragment
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AddOfflineBookFragment : Fragment() {

    private var _binding: FragmentAddBookOfflineInputBinding? = null
    private val viewModel: AddBookViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("WrongConstant")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBookOfflineInputBinding.inflate(inflater, container, false)

        addIfRequestedToAddABook()
        setupIsBookCreatedObserver()
        setupOnClickAddBook()


        binding.etBookDescription
        binding.etBookDescription.editText?.justificationMode = 1


        return binding.root
    }

    private fun addIfRequestedToAddABook() {
        val book = arguments?.getParcelable<BookSearch>("book")
        if (book != null) {
            binding.apply {
                etBookTitle.editText?.setText(book.title)
                etBookAuthor.editText?.setText(book.author)
                etBookIsbn10Or13.editText?.setText(getISBNNotEmpty(book))
                etBookDescription.editText?.setText(book.description)
                etBookPublisher.editText?.setText(book.publisher)
                if (book.thumbnail.isNotEmpty())
                    Glide.with(requireActivity()).load(book.thumbnail)
                        .centerCrop()
                        .apply(RequestOptions().override(400, 600))
                        .into(ivBookPreview)
            }
        }
    }

    private fun getISBNNotEmpty(book: BookSearch) =
        if (book.ISBN_13.isNullOrEmpty()) book.ISBN_10 else book.ISBN_13

    private fun setupIsBookCreatedObserver() {
        viewModel.isBookCreatedResponse.observe(viewLifecycleOwner) {
            binding.progressSending.visibility = View.GONE

            if (it is Result.Success) {
                Toast.makeText(requireContext(), "Book Created", Toast.LENGTH_LONG).show()
                val resultIntent = Intent()
                activity?.setResult(BookListFragment.BOOK_ADDED, resultIntent)
                requireActivity().finish()
            } else {
                val errorMessage = (it as Result.Error).errorBody.message
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupOnClickAddBook() {
        val btnAddBook = binding.btnAddBook

        btnAddBook.setOnClickListener {
            val isValid: Boolean = validateInputs()
            if (isValid) {
                binding.progressSending.visibility = View.VISIBLE
                createBook()
            }
        }
    }


    private fun createBook() {
        val title = binding.etBookTitle.editText!!.text.toString()
        val author = binding.etBookAuthor.editText!!.text.toString()
        val publisher = binding.etBookPublisher.editText!!.text.toString()
        val description = binding.etBookDescription.editText!!.text.toString()
        val isbn = binding.etBookIsbn10Or13.editText!!.text.toString()
        val prefs = requireActivity().getSharedPreferences(
            Constants.Prefs.USER_TOKENS,
            AppCompatActivity.MODE_PRIVATE
        )
        val accessToken = prefs.getString(Constants.Prefs.Tokens.ACCESS_TOKEN, "")!!

        val book = arguments?.getParcelable<BookSearch>("book")
        val thumbnail = book?.thumbnail ?: ""

        viewModel.apiCreateBook(
            accessToken = accessToken,
            title = title,
            author = author,
            publisher = publisher,
            isbn = isbn,
            description = description,
            thumbnail = thumbnail
        )
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}