package com.bibliotecadebolso.app.ui.bookInfo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.ActivityBookInfoBinding
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class BookInfoActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityBookInfoBinding
    private lateinit var viewModel: BookInfoViewModel
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[BookInfoViewModel::class.java]

        setSupportActionBar(binding.toolbar)

        val extras = intent.extras
        val bookId = extras?.getInt("id", -1) ?: -1

        if (bookId == -1) {
            Toast.makeText(this, getString(R.string.label_book_not_found), Toast.LENGTH_LONG).show()
            finishAffinity()
        }

        val prefs = getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)
        val accessToken = prefs.getString(Constants.Prefs.Tokens.ACCESS_TOKEN, "")!!

        viewModel.getInfoByID(accessToken, bookId)

        val spinnerReadingStatusKey =
            resources.getStringArray(R.array.spinner_book_reading_status_key)
        val spinnerReadingStatusValue =
            resources.getStringArray(R.array.spinner_book_reading_status_value)

        val readingStatusMap = HashMap<String, String>()
        for (i in spinnerReadingStatusKey.indices) {
            readingStatusMap[spinnerReadingStatusValue[i]] = spinnerReadingStatusKey[i]
        }

        val spinner = binding.spinnerReadingStatus
        spinner.onItemSelectedListener = this

        ArrayAdapter.createFromResource(
            this,
            R.array.spinner_book_reading_status_value,
            R.layout.spinner_item
        ).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = arrayAdapter
        }

        readingStatusMap[spinner.selectedItem.toString()]?.let { Log.i("BookInfoActivity", it) }
        viewModel.mutableBookInfo.observe(this) {
            if (it is Result.Success) {
                val book = it.response
                binding.tvBookTitle.text = book.title
                binding.etBookAuthor.editText?.setText(book.author)
                if (book.thumbnail.isNotEmpty()) {
                    Glide.with(this).load(book.thumbnail)
                        .centerCrop()
                        .apply(RequestOptions().override(300, 450))
                        .into(binding.ivBookPreview)
                }
                if (book.description.length > 270) {
                    binding.tvDescription.text = book.description.substring(0, 270) + "..."
                } else {
                    binding.tvDescription.text = book.description
                }

                binding.tvDescriptionShowMore.setOnClickListener {
                    Log.i("tvDescriptionShowMore", "Clicou aqui")
                    binding.tvDescription.text = book.description
                }
                Log.e("tvDescriptionShowMore",binding.tvDescriptionShowMore.toString())
            }
        }
    }


    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Log.i("BookActivityOnItemSelected", p0?.getItemAtPosition(p2).toString())
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}