package com.bibliotecadebolso.app.ui.bookInfo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.databinding.ActivityBookInfoBinding
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.SharedPreferencesUtils
import com.bumptech.glide.Glide

class BookInfoActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityBookInfoBinding
    private lateinit var viewModel: BookInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[BookInfoViewModel::class.java]
        setSupportActionBar(binding.toolbar)

        val bookId = getIdFromExtrasOrMinus1()
        finishActivityIfBookIdIsInvalid(bookId)

        val prefs = getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)
        val accessToken = SharedPreferencesUtils.getAccessToken(prefs)
        viewModel.getInfoByID(accessToken, bookId)

        setupReadingStatusSpinner()
        listenerFillActivityWithBookInfo()
    }

    private fun getIdFromExtrasOrMinus1(): Int {
        val extras = intent.extras
        return extras?.getInt("id", -1) ?: -1
    }

    private fun finishActivityIfBookIdIsInvalid(bookId: Any) {
        if (bookId == -1) {
            Toast.makeText(this, getString(R.string.label_book_not_found), Toast.LENGTH_LONG).show()
            finishAffinity()
        }
    }

    private fun setupReadingStatusSpinner() {
        val spinnerReadingStatusKey =
            resources.getStringArray(R.array.spinner_book_reading_status_key)
        val spinnerReadingStatusValue =
            resources.getStringArray(R.array.spinner_book_reading_status_value)

        val readingStatusMap = HashMap<String, String>()
        for (i in spinnerReadingStatusKey.indices) {
            readingStatusMap[spinnerReadingStatusValue[i]] = spinnerReadingStatusKey[i]
        }
        binding.spinnerReadingStatus.onItemSelectedListener = this

        ArrayAdapter.createFromResource(
            this,
            R.array.spinner_book_reading_status_value,
            R.layout.spinner_item
        ).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerReadingStatus.adapter = arrayAdapter
        }
    }


    private fun listenerFillActivityWithBookInfo() {
        viewModel.liveDataBookInfo.observe(this) {
            if (it is Result.Success) {
                loadActivityWithBookInfo(it.response)

            }
        }
    }

    private fun loadActivityWithBookInfo(book: Book) {
        binding.tvBookTitle.text = book.title
        binding.etBookAuthor.editText?.setText(book.author)
        if (book.thumbnail.isNotEmpty()) {
            Glide.with(this).load(book.thumbnail)
                .centerInside()
                .into(binding.ivBookPreview)
        }
        if (book.description.length > 270) {
            binding.tvDescription.text = book.description.substring(0, 270) + "..."
        } else {
            binding.tvDescription.text = book.description
        }

        binding.tvDescriptionShowMore.setOnClickListener {
            binding.tvDescription.text = book.description
        }
    }



    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Log.i("BookActivityOnItemSelected", p0?.getItemAtPosition(p2).toString())
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Glide.with(this).clear(binding.ivBookPreview)
    }
}