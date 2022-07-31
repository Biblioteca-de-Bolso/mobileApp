package com.bibliotecadebolso.app.ui.bookInfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.ActivityBookInfoBinding
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result

class BookInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookInfoBinding
    private lateinit var viewModel: BookInfoViewModel
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

        viewModel.mutableBookInfo.observe(this) {
            if (it is Result.Success) {
                val book = it.response
                binding.tvBookTitle.text = book.title
                binding.etBookAuthor.editText?.setText(book.author)
            }
        }




    }
}