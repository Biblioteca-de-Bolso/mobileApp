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

        setEnumChoosed(enumString)
        viewModel = ViewModelProvider(this)[BookListViewModel::class.java]

        setupRecyclerView()
        setupBookListObserver()
        setupSearchListListener()

        if (viewModel.normalList.bookListLiveData.value == null) getList()

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = getString(R.string.label_search)

        searchView.setQuery(viewModel.searchList.searchContent, true)
        searchView.setOnSearchClickListener {
            viewModel.listType = ListType.SEARCH
            changeList()
        }
        searchView.setOnCloseListener {
            viewModel.listType = ListType.NORMAL_LIST
            scrollState.setAllBooleanAs(false)
            changeList()

            true
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0.isNullOrEmpty()) {
                    viewModel.listType = ListType.NORMAL_LIST
                    changeList()
                } else {
                    viewModel.listType = ListType.SEARCH
                    viewModel.searchList.searchContent = p0.toString()
                    getSearchList(viewModel.searchList.searchContent, true)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0.isNullOrEmpty()) {
                    scrollState.setAllBooleanAs(false)
                    viewModel.listType = ListType.NORMAL_LIST
                    changeList()
                    return true
                }
                return false
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun changeList() {
        Log.e("listType", viewModel.listType.toString())
        when (viewModel.listType) {
            ListType.NORMAL_LIST -> {
                bookListAdapter.differ.submitList(
                    viewModel.normalList.bookListPreviousSuccessResponse!!
                )
                bookListAdapter.notifyDataSetChanged()
            }
            ListType.SEARCH -> bookListAdapter.differ.submitList(
                viewModel.searchList.bookListPreviousSuccessResponse ?: emptyList()
            )
        }
    }

    private fun getList() {
        val prefs = getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)
        val accessToken = SharedPreferencesUtils.getAccessToken(prefs)

        if (viewModel.bookListReachedOnTheEnd()) {
            showLongSnackBar(BookListViewModel.reachedOnTheEndErrorResponse().message)
            return
        }

        binding.pgLoading.visibility = View.VISIBLE
        viewModel.getBookList(accessToken, readStatusEnum = enumChoosed)

    }

    private fun getSearchList(searchContent: String, newSearchContent: Boolean = false) {
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
                    showLongSnackBar(it.errorBody.message)
                }
            }
            scrollState.setAllBooleanAs(false)
            binding.pgLoading.visibility = View.GONE
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

    private fun setupBookListObserver() {
        viewModel.normalList.bookListLiveData.observe(this) {
            when (it) {
                is Result.Success -> {
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
                    when (viewModel.listType) {
                        ListType.NORMAL_LIST -> getList()
                        ListType.SEARCH -> getSearchList(viewModel.searchList.searchContent)
                    }
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