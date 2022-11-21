package com.bibliotecadebolso.app.ui.book.linearList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.bibliotecadebolso.app.databinding.ActivityBookListBinding
import com.bibliotecadebolso.app.ui.ResultCodes
import com.bibliotecadebolso.app.ui.adapter.BookLinearListAdapter
import com.bibliotecadebolso.app.ui.book.bookInfo.BookInfoActivity
import com.bibliotecadebolso.app.util.*
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @param readStatusEnum read status enum, can be null
 * @param toReturnEnum can be null
 * @param showBorrowStatus show borrow status. Default value: false
 * @param cancelClickIfBorrowed cancel click if book is currently borrowed. Default value: false
 **/
class BookListActivity : AppCompatActivity(), RvOnClickListener {
    private lateinit var binding: ActivityBookListBinding
    private lateinit var bookListAdapter: BookLinearListAdapter
    private lateinit var viewModel: BookListViewModel
    val scrollState: ScrollState = ScrollState()
    private var enumChoosed: ReadStatusEnum? = null
    private var toReturnEnum: TO_RETURN = TO_RETURN.NOTHING
    private var showBorrowStatus: Boolean = false
    private var cancelClickIfBorrowed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookListBinding.inflate(layoutInflater)
        ContextUtils.setActionBarColor(supportActionBar, this)
        val actionBarTitle = getString(R.string.label_book)
        supportActionBar?.title = actionBarTitle

        viewModel = ViewModelProvider(this)[BookListViewModel::class.java]
        initVariables()
        setupRecyclerView()
        setupSearchListListener()
        updateBookInfoObserve()

        if (viewModel.searchList.listLiveData.value == null) getSearchList(null, true)

        setContentView(binding.root)
    }

    private fun initVariables() {
        val enumString = intent.extras?.getString("readStatusEnum")
        val toReturnEnumChoosed = intent.extras?.get("toReturnEnum") as TO_RETURN?
        showBorrowStatus =
            intent.extras?.getBoolean("showBorrowStatus", false) ?: false
        cancelClickIfBorrowed =
            intent.extras?.getBoolean("cancelClickIfBorrowed", false) ?: false
        setEnumChoosed(enumString)
        setToReturnEnumChoosed(toReturnEnumChoosed)
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

    private fun setToReturnEnumChoosed(toReturnEnumChoosed: TO_RETURN?) {
        toReturnEnum = toReturnEnumChoosed ?: TO_RETURN.NOTHING
    }

    private fun setupRecyclerView() {
        bookListAdapter = BookLinearListAdapter(this, this, showBorrowStatus, cancelClickIfBorrowed)
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
        viewModel.searchList.listLiveData.observe(this) {
            when (it) {
                is Result.Success -> {
                    bookListAdapter.differ.submitList(it.response)
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
        when (toReturnEnum) {
            TO_RETURN.NOTHING -> startBookInfoActivity(position)
            TO_RETURN.BOOKID -> returnBookId(position)
        }
    }

    private fun startBookInfoActivity(id: Int) {
        val intent = Intent(this, BookInfoActivity::class.java)
        intent.putExtra("id", id)
        bookInfoActivityResult.launch(intent)
    }

    private fun returnBookId(id: Int) {
        val returnResult = Intent()
        returnResult.putExtra("bookId", id)
        setResult(RETURN_BOOK_ID, returnResult)
        finish()

    }

    private val bookInfoActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        when (result.resultCode) {
            ResultCodes.BOOK_REMOVED -> {
                removeBookFromList(result.data!!.extras!!.getInt("id"))
            }
            ResultCodes.BOOK_UPDATED_STATUS -> {
                if (statusDifferentFromEnumChoosed(result.data!!.extras!!.get("readStatusEnum") as ReadStatusEnum)) {
                    removeBookFromList(result.data!!.extras!!.getInt("id"))
                }
            }
            ResultCodes.BOOK_EDITED -> {
                updateBookInfo(result.data!!.extras!!.getInt("id"))
            }

            ResultCodes.BOOK_EDITED_AND_UPDATED_STATUS -> {
                if (statusDifferentFromEnumChoosed(result.data!!.extras!!.get("readStatusEnum") as ReadStatusEnum)) {
                    removeBookFromList(result.data!!.extras!!.getInt("id"))
                } else {
                    updateBookInfo(result.data!!.extras!!.getInt("id"))
                }
            }
        }
    }

    private fun statusDifferentFromEnumChoosed(readStatusEnum: ReadStatusEnum) =
        enumChoosed != null && readStatusEnum != enumChoosed

    private fun removeBookFromList(id: Int) {
        val list = viewModel.searchList.listLastSucessfullyResponse!!
        val index = list.indexOfFirst { it.id == id }
        list.remove(list.find { it.id == id })
        bookListAdapter.differ.submitList(list)
        bookListAdapter.notifyItemRemoved(index)
    }

    private fun updateBookInfo(id: Int) {
        val prefs = getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)
        val accessToken = SharedPreferencesUtils.getAccessToken(prefs)
        viewModel.getInfoByID(accessToken, id)
    }

    private fun updateBookInfoObserve() {
        viewModel.generalLiveDataInfo.observe(this) {
            when (it) {
                is Result.Success -> {
                    val book = it.response
                    val list = viewModel.searchList.listLastSucessfullyResponse!!
                    val bookOnList = list.find { bookList -> bookList.id == book.id!! }
                    val index = list.indexOfFirst { bookList -> bookList.id == book.id!! }

                    bookOnList?.let {
                        list[index] = bookOnList.copy(
                            author = book.author,
                            title = book.title,
                            isbn10 = book.isbn10,
                            isbn13 = book.isbn13,
                        )

                    }



                    bookListAdapter.differ.submitList(list)
                }
                else -> {}
            }
        }
    }

    companion object {
        const val RETURN_BOOK_ID = 21

        public enum class TO_RETURN {
            NOTHING,
            BOOKID,
        }
    }
}

