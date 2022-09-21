package com.bibliotecadebolso.app.ui.book.gridList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.ReadStatusEnum
import com.bibliotecadebolso.app.databinding.ActivityBookListBinding
import com.bibliotecadebolso.app.ui.adapter.BookLinearListAdapter
import com.bibliotecadebolso.app.ui.bookInfo.BookInfoActivity
import com.bibliotecadebolso.app.ui.home.ui.bookList.BookListFragment
import com.bibliotecadebolso.app.util.*
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class BookListActivity : AppCompatActivity(), RvOnClickListener {
    private lateinit var binding: ActivityBookListBinding
    private lateinit var bookListAdapter: BookLinearListAdapter
    private lateinit var viewModel: BookListViewModel
    val scrollState: ScrollState = ScrollState()
    private var enumChoosed: ReadStatusEnum? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookListBinding.inflate(layoutInflater)
        ContextUtils.setActionBarColor(supportActionBar, this)
        val actionBarTitle = getString(R.string.label_book)
        supportActionBar?.title = actionBarTitle

        val enumString = intent.extras?.getString("readStatusEnum")

        if (enumString != null) {
            try {
                enumChoosed = ReadStatusEnum.valueOf(enumString)
            } catch (e: IllegalArgumentException) {
                Toast.makeText(
                    this,
                    getString(R.string.label_invalid_read_status),
                    Toast.LENGTH_SHORT
                ).show()
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

        if (viewModel.bookListReachedOnTheEnd()) {
            showLongSnackBar(viewModel.reachedOnTheEndErrorResponse().message)
            return
        }

        binding.pgLoading.visibility = View.VISIBLE
        viewModel.getBookList(accessToken, readStatusEnum = enumChoosed)

    }


    private fun setupRecyclerView() {
        bookListAdapter = BookLinearListAdapter(this, this)
        val layoutManager = LinearLayoutManager(this)

        binding.rvListBook.apply {
            setLayoutManager(layoutManager)
            adapter = bookListAdapter
            addOnScrollListener(listenerGetContentOnScrollOnBottom)
        }
    }

    private fun setupBookListObserver() {
        viewModel.bookListLiveData.observe(this) {
            when (it) {
                is Result.Success -> {
                    val oldCount = bookListAdapter.itemCount
                    val newCount = oldCount + it.response.size
                    bookListAdapter.differ.submitList(it.response)
                }
                is Result.Error -> {
                    showLongSnackBar(it.errorBody.message)
                }
            }
            scrollState.setAllBooleanAs(false)
            binding.pgLoading.visibility = View.GONE
        }
    }

    private val listenerGetContentOnScrollOnBottom = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1)) {
                if (!scrollState.scrollOnBottom && !scrollState.isLoadingNewItems) {
                    scrollState.setAllBooleanAs(true)
                    getList()
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
        bookInfoActivityResult.launch(intent)

        //val modalBottomSheet = BookItemBottomSheet(position)
        //modalBottomSheet.show(this.parentFragmentManager, BookItemBottomSheet.TAG)
    }

    private val bookInfoActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == BookListFragment.REMOVE_BOOK) {
            val list = bookListAdapter.differ.currentList.toMutableList()
            list.remove(list.find { it.id == result.data!!.extras!!.getInt("id") })
            bookListAdapter.differ.submitList(list)
        }
    }
}



data class ScrollState(
    var isLoadingNewItems: Boolean = false,
    var scrollOnBottom: Boolean = false,
) {
    fun setAllBooleanAs(booleanState: Boolean) {
        isLoadingNewItems = booleanState
        scrollOnBottom = booleanState
    }
}