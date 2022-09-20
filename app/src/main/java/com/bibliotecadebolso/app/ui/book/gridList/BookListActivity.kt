package com.bibliotecadebolso.app.ui.book.gridList

import BookListDividerDecoration
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.ReadStatusEnum
import com.bibliotecadebolso.app.databinding.ActivityBookListBinding
import com.bibliotecadebolso.app.ui.adapter.BookLinearListAdapter
import com.bibliotecadebolso.app.ui.adapter.BookListAdapter
import com.bibliotecadebolso.app.ui.bookInfo.BookInfoActivity
import com.bibliotecadebolso.app.ui.home.ui.bookList.BookListFragment
import com.bibliotecadebolso.app.util.*
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class BookListActivity : AppCompatActivity(), RvOnClickListener {
    private lateinit var binding: ActivityBookListBinding
    private lateinit var bookListAdapter: BookLinearListAdapter
    private lateinit var viewModel: BookListViewModel
    var isLoadingNewItems = false
    var scrollWasOnBottom = false
    private var enumChoosed: ReadStatusEnum? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookListBinding.inflate(layoutInflater)
        ContextUtils.setActionBarColor(supportActionBar, this)
        val actionBarTitle = getString(R.string.label_book)
        supportActionBar?.title = actionBarTitle

        val enumString = intent.extras?.getString("readStatusEnum")

        if (enumString != null){
            try {
                enumChoosed = ReadStatusEnum.valueOf(enumString)
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, getString(R.string.label_invalid_read_status), Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
        }

        viewModel = ViewModelProvider(this)[BookListViewModel::class.java]

        setupRecyclerView()
        setupBookListObserver()

        if (viewModel.bookListLiveData.value == null) getList()
        setContentView(binding.root)
    }

    private fun getList() {
        val prefs = getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)
        val accessToken = SharedPreferencesUtils.getAccessToken(prefs)

        viewModel.getBookList(accessToken, readStatusEnum = enumChoosed)

    }


    private fun setupRecyclerView() {
        bookListAdapter = BookLinearListAdapter(this, this)
        val layoutManager = LinearLayoutManager(this)

        binding.rvListBook.apply {
            setLayoutManager(layoutManager)
            adapter = bookListAdapter
        }
    }

    private fun setupBookListObserver() {
        viewModel.bookListLiveData.observe(this) {
            when (it) {
                is Result.Success -> {
                    bookListAdapter.differ.submitList(it.response)
                }
                is Result.Error -> {
                    showLongSnackBar(it.errorBody.message)
                }
            }
        }
    }

    private fun showLongSnackBar(message: String) {
        Snackbar.make(binding.root, message, BaseTransientBottomBar.LENGTH_LONG)
            .show()
    }

    override fun onItemCLick(position: Int) {
        val intent = Intent(this, BookInfoActivity::class.java)
        intent.putExtra("id", position)
        startActivityForResult(intent, BookListFragment.VIEW_DETAIL_BOOK)

        //val modalBottomSheet = BookItemBottomSheet(position)
        //modalBottomSheet.show(this.parentFragmentManager, BookItemBottomSheet.TAG)
    }

}