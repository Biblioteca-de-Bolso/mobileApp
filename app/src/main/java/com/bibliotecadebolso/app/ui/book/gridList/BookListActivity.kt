package com.bibliotecadebolso.app.ui.book.gridList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.ReadStatusEnum
import com.bibliotecadebolso.app.data.model.app.scroll.ScrollState
import com.bibliotecadebolso.app.data.model.request.BorrowStatus
import com.bibliotecadebolso.app.databinding.ActivityBookListBinding
import com.bibliotecadebolso.app.ui.adapter.BookLinearListAdapter
import com.bibliotecadebolso.app.ui.book.bookInfo.BookInfoActivity
import com.bibliotecadebolso.app.ui.home.ui.bookList.BookListFragment
import com.bibliotecadebolso.app.util.*
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

        setEnumChoosed(enumString)
        viewModel = ViewModelProvider(this)[BookListViewModel::class.java]

        setupRecyclerView()
        setupSearchListListener()

        if (viewModel.searchList.bookListLiveData.value == null) getSearchList(null, true)

        setContentView(binding.root)
    }

    private fun setEnumChoosed(enumString: String?) {
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

    private val listenerGetContentOnScrollOnBottom = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1)) {
                if (!scrollState.scrollOnBottom && !scrollState.isLoadingNewItems) {
                    scrollState.setAllBooleanAs(true)
                    getSearchList(viewModel.searchList.searchContent)
                }
            }
        }
    }

    private fun getSearchList(searchContent: String?, newSearchContent: Boolean = false) {
        val prefs = getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)
        val accessToken = SharedPreferencesUtils.getAccessToken(prefs)

        if (!newSearchContent && viewModel.searchList.bookListReachedOnTheEnd()) {
            showLongSnackBar(BookListViewModel.reachedOnTheEndErrorResponse().message)
        } else {

            binding.pgLoading.visibility = View.VISIBLE
            viewModel.searchBook(
                accessToken,
                searchContent,
                readStatusEnum = enumChoosed,
                newSearchContent = newSearchContent
            )
        }
    }

    private fun setupSearchListListener() {
        viewModel.searchList.bookListLiveData.observe(this) {
            when (it) {
                is Result.Success -> {
                    bookListAdapter.differ.submitList(it.response)
                    Log.e("ListSize", it.response.size.toString())
                }
                is Result.Error -> {
                    when (it.errorBody.code) {
                        "reachedOnTheEnd" -> showLongSnackBar(getString(R.string.label_reached_in_the_end))
                        else -> showLongSnackBar(it.errorBody.message)
                    }
                }
            }
            scrollState.setAllBooleanAs(false)
            binding.pgLoading.visibility = View.GONE
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = getString(R.string.label_search)

        searchView.setQuery(viewModel.searchList.searchContent, true)
        searchView.setOnSearchClickListener {
            bookListAdapter.differ.submitList(emptyList())
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            var job: Job? = null

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                job?.cancel()
                job = MainScope().launch {
                    delay(Constants.SEARCH_NEWS_DELAY)
                    val oldContent = viewModel.searchList.searchContent
                    val newContent =
                        if (p0.isNullOrEmpty()) null
                        else p0.toString()

                    viewModel.searchList.searchContent = newContent
                    val isNewContent = oldContent != newContent
                    getSearchList(viewModel.searchList.searchContent, isNewContent)
                }
                return false
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun showLongSnackBar(message: String) {
        Snackbar.make(binding.root, message, BaseTransientBottomBar.LENGTH_LONG)
            .show()
    }

    override fun onItemCLick(position: Int) {
        val intent = Intent(this, BookInfoActivity::class.java)
        intent.putExtra("id", position)
        bookInfoActivityResult.launch(intent)
    }

    private val bookInfoActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == BookListFragment.REMOVE_BOOK) {
            val list = viewModel.searchList.bookListPreviousSuccessResponse!!.toMutableList()
            list.remove(list.find { it.id == result.data!!.extras!!.getInt("id") })
            bookListAdapter.differ.submitList(list)
        }
    }
}

