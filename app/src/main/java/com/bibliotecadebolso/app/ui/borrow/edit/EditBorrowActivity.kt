package com.bibliotecadebolso.app.ui.borrow.edit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.request.Borrow
import com.bibliotecadebolso.app.data.model.request.BorrowStatus
import com.bibliotecadebolso.app.data.model.request.EditBorrow
import com.bibliotecadebolso.app.data.validator.ValidationResultUtils
import com.bibliotecadebolso.app.data.validator.validations.ContactNameValidator
import com.bibliotecadebolso.app.databinding.ActivityEditBorrowBinding
import com.bibliotecadebolso.app.ui.ResultCodes
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.SharedPreferencesUtils
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @param borrowId bookId as Int. If pass null, activity will finish
 */
class EditBorrowActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityEditBorrowBinding
    private lateinit var viewModel: EditBorrowViewModel
    private lateinit var borrowStatusAdapter: ArrayAdapter<CharSequence>
    private var borrowId: Int? = null
    private var userIsInteracting = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initVariables()
        setupBorrowStatusSpinner()
        setupInputListener()

        getBorrowByIdListener()
        getBookByIdListener()
        setEditBorrowListener()
        setRemoveBorrowListener()

        setBtnOnClickRemoveBorrow()
        setBtnOnClickEditBorrow()

        setContentView(binding.root)

        lifecycleScope.launch {
            delay(200L)
            getBorrow()
        }
    }

    private fun initVariables() {

        binding = ActivityEditBorrowBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[EditBorrowViewModel::class.java]

        borrowId = intent.extras?.getInt("borrowId")
        if (borrowId == 0) borrowId = null
    }

    private fun setupBorrowStatusSpinner() {
        viewModel.spinner.setReadStatusValuesKeyMap(
            resources.getStringArray(R.array.spinner_book_borrow_status_key),
            resources.getStringArray(R.array.spinner_book_borrow_status_value)
        )
        binding.spinnerBorrowStatus.onItemSelectedListener = this

        borrowStatusAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.spinner_book_borrow_status_value,
            R.layout.spinner_item
        )

        with(borrowStatusAdapter) {
            this.setDropDownViewResource(R.layout.spinner_dropdown_item)
            binding.spinnerBorrowStatus.adapter = this
        }
    }

    private fun setupInputListener() {
        binding.tilContactName.editText?.addTextChangedListener {
            viewModel.inputs.contactName = it.toString()
        }
    }

    private fun getBorrowByIdListener() {
        viewModel.borrowLiveData.observe(this) {
            binding.pgLoading.visibility = View.GONE
            when (it) {
                is Result.Success -> loadContent(it.response)
                is Result.Error -> {
                    Toast.makeText(this, it.errorBody.message, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        binding.btnEditLoan.isEnabled = true
        binding.btnRemoveBorrow.isEnabled = true
    }

    private fun loadContent(borrow: Borrow) {

        binding.tilContactName.editText?.setText(borrow.contactName)
        binding.pgLoading.visibility = View.VISIBLE
        getBookById(borrow.bookId)
        selectBorrowStatusInSpinner(borrow.borrowStatus)
    }

    private fun getBookById(bookId: Int) {
        val accessToken = SharedPreferencesUtils.getAccessToken(
            getSharedPreferences(
                Constants.Prefs.USER_TOKENS,
                MODE_PRIVATE
            )
        )
        viewModel.getBookById(accessToken, bookId)
    }

    private fun getBookByIdListener() {
        viewModel.bookLiveData.observe(this) {
            binding.pgLoading.visibility = View.GONE
            when (it) {
                is Result.Success -> loadBookContent(it.response)
                is Result.Error -> {
                    Toast.makeText(this, it.errorBody.message, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

    }

    private fun loadBookContent(book: Book) {
        binding.itemBook.apply {

            val defaultThumbnailInt = R.drawable.ic_item_book

            tvTitle.text = book.title
            tvAuthor.text = book.author
            tvIsbn10.text = "ISBN-10: ${book.isbn10}"
            tvIsbn13.text = "ISBN-13: ${book.isbn13}"

            Glide.with(this@EditBorrowActivity)
                .load(book.thumbnail.ifEmpty { defaultThumbnailInt })
                .centerInside()
                .into(ivThumbnail)
        }
    }

    private fun selectBorrowStatusInSpinner(borrowStatus: BorrowStatus?) {
        if (borrowStatus != null) {
            val indexOfBorrowStatusLabel =
                viewModel.spinner.getReadStatusEnumIndexOnSpinner(borrowStatus)
            val readingStatusKey =
                viewModel.spinner.getReadingStatusKeyByIndex(indexOfBorrowStatusLabel)
            binding.spinnerBorrowStatus.setSelection(
                borrowStatusAdapter.getPosition(readingStatusKey)
            )

            viewModel.inputs.borrowStatusSelected = borrowStatus
        }
    }

    private fun setEditBorrowListener() {
        binding.pgLoading.visibility = View.GONE
        viewModel.editBorrowLiveData.observe(this) {
            when (it) {
                is Result.Success -> {
                    val returnResult = Intent()
                    returnResult.putExtra("id", it.response.id)
                    setResult(ResultCodes.BORROW_UPDATED, returnResult)
                    finish()
                }
                is Result.Error -> {
                    Toast.makeText(this, it.errorBody.message, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun setRemoveBorrowListener() {
        binding.pgLoading.visibility = View.GONE
        viewModel.removeBorrowLiveData.observe(this) {
            when (it) {
                is Result.Success -> {
                    val returnResult = Intent()
                    returnResult.putExtra("id", borrowId)
                    setResult(ResultCodes.BORROW_DELETED, returnResult)
                    finish()
                }
                is Result.Error -> {
                    Toast.makeText(this, it.errorBody.message, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun setBtnOnClickRemoveBorrow() {
        binding.btnRemoveBorrow.setOnClickListener {
            if (borrowId == null) return@setOnClickListener

            val accessToken = SharedPreferencesUtils.getAccessToken(
                getSharedPreferences(
                    Constants.Prefs.USER_TOKENS,
                    MODE_PRIVATE
                )
            )

            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.label_are_you_sure))
                .setMessage(getString(R.string.label_are_you_sure_delete_borrow))
                .setNegativeButton(getString(R.string.label_cancel)) { _, _ -> }
                .setPositiveButton(resources.getString(R.string.label_delete)) { dialog, which ->

                    binding.pgLoading.visibility = View.VISIBLE
                    viewModel.removeBorrow(
                        accessToken,
                        borrowId!!
                    )
                }
                .show()
        }


    }

    private fun setBtnOnClickEditBorrow() {
        binding.btnEditLoan.setOnClickListener {
            val hasError = ValidationResultUtils.showErrorOnTextInputLayoutAndReturnIfHasError(
                this,
                mapOf(binding.tilContactName to ContactNameValidator(viewModel.inputs.contactName))
            )
            if (hasError) return@setOnClickListener

            val accessToken = SharedPreferencesUtils.getAccessToken(
                getSharedPreferences(
                    Constants.Prefs.USER_TOKENS,
                    MODE_PRIVATE
                )
            )

            binding.pgLoading.visibility = View.VISIBLE
            viewModel.editBorrow(
                accessToken,
                EditBorrow(
                    borrowId!!,
                    viewModel.inputs.borrowStatusSelected,
                    viewModel.inputs.contactName
                )
            )
        }
    }

    private fun getBorrow() {
        if (borrowId == null || borrowId == -1) finish()
        if (viewModel.inputs.contactName.isNotEmpty()) return

        val accessToken = SharedPreferencesUtils.getAccessToken(
            getSharedPreferences(
                Constants.Prefs.USER_TOKENS,
                MODE_PRIVATE
            )
        )

        binding.pgLoading.visibility = View.VISIBLE
        viewModel.getBorrowById(accessToken, borrowId!!)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (!userIsInteracting) return
        val itemLabelSelected: String = p0?.getItemAtPosition(p2).toString()
        val borrowStatusSelected: BorrowStatus =
            getBorrowStatusOrGetPendingIfNull(itemLabelSelected)

        viewModel.inputs.borrowStatusSelected = borrowStatusSelected

    }

    private fun getBorrowStatusOrGetPendingIfNull(itemLabelSelected: String): BorrowStatus {
        return viewModel.spinner.borrowStatusValuesKey[itemLabelSelected]?.let { borrowStatusString: String ->
            try {
                BorrowStatus.valueOf(borrowStatusString)
            } catch (e: IllegalArgumentException) {
                BorrowStatus.PENDING
            }
        } ?: BorrowStatus.PENDING
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        userIsInteracting = true
    }
}