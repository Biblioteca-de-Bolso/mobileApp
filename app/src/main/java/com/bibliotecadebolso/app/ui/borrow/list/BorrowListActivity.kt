package com.bibliotecadebolso.app.ui.borrow.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.request.Borrow
import com.bibliotecadebolso.app.data.model.request.BorrowStatus
import com.bibliotecadebolso.app.databinding.ActivityBorrowListBinding
import com.bibliotecadebolso.app.ui.adapter.BorrowAdapter
import com.bibliotecadebolso.app.ui.book.gridList.BookListViewModel
import com.bibliotecadebolso.app.ui.borrow.edit.EditBorrowActivity
import com.bibliotecadebolso.app.util.*
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

/**
 * @param borrowStatus can be null or BorrowStatusEnum value
 */
class BorrowListActivity : AppCompatActivity(), RvOnClickListener {

    private lateinit var binding: ActivityBorrowListBinding
    private lateinit var borrowListAdapter: BorrowAdapter
    private lateinit var viewModel: BorrowListViewModel
    var borrowStatus: BorrowStatus? = null
    var bookId: Int? = null

    private var searchViewName: SearchView? = null
    private var searchViewBook: SearchView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBorrowListBinding.inflate(layoutInflater)

        ContextUtils.setActionBarColor(supportActionBar, this)
        setCustomSearchSupportActionBar()
        initVariables()
        setupRecyclerView()
        binding.pgLoading.visibility = View.GONE

        setToolBarCustomSearchListener()
        searchListListener()
        setContentView(binding.root)

        lifecycleScope.launch {
            delay(500L)
            getSearchList(null,true)
        }
    }


    override fun finishAfterTransition() {
        super.finishAfterTransition()
        getSearchList(null,true)
    }

    private fun setCustomSearchSupportActionBar() {
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.custom_search_borrow, null)
        supportActionBar?.customView = view
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
    }

    private fun initVariables() {
        borrowStatus = intent.extras?.get("borrowStatus") as BorrowStatus?
        bookId = intent.extras?.getInt("bookId")
        if (bookId != null && bookId == 0 ) bookId = null
        viewModel = ViewModelProvider(this)[BorrowListViewModel::class.java]
        val actionBarCustomView = supportActionBar?.customView
        searchViewName = actionBarCustomView?.findViewById(R.id.search1)

        setupMockContent(actionBarCustomView)
    }

    private fun setupMockContent(actionBarCustomView: View?) {
        val bookImage = actionBarCustomView?.findViewById<ImageView>(R.id.iv_book_custom_search_borrow)
        bookImage?.setOnClickListener {
            val mutableList = mutableListOf<Borrow>()
            when (borrowStatus) {
                BorrowStatus.RETURNED -> BorrowGeneratorUtils.fillListWithOnlyReturned(10,mutableList)
                else -> BorrowGeneratorUtils.fillListWithOnlyPending(10,mutableList)
            }

            viewModel.searchList.listLastSucessfullyResponse = mutableListOf()
            viewModel.searchList.listLiveData.postValue(Result.Success<List<Borrow>>(mutableList))

        }
    }

    private fun fillRecyclerViewWithBorrowList() {
        val list = mutableListOf<Borrow>()

        when (borrowStatus) {
            null -> BorrowGeneratorUtils.fillListWithOnlyReturned(10, list)
            BorrowStatus.PENDING -> viewModel
            BorrowStatus.RETURNED -> BorrowGeneratorUtils.fillListWithOnlyReturned(10, list)
        }

        borrowListAdapter.differ.submitList(list)
    }

    private fun setupRecyclerView() {
        borrowListAdapter = BorrowAdapter(this, this)
        binding.rvListBorrow.adapter = borrowListAdapter
        binding.rvListBorrow.layoutManager = LinearLayoutManager(this)
    }

    private fun setToolBarCustomSearchListener() {
        searchViewName?.setOnQueryTextListener(queryListener)
        searchViewBook?.setOnQueryTextListener(queryListener)
    }

    private val queryListener = object : SearchView.OnQueryTextListener {
        var job: Job? = null

        override fun onQueryTextSubmit(p0: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(p0: String?): Boolean {
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_NEWS_DELAY)
                val oldContent = viewModel.searchList.searchContent
                viewModel.searchList.searchContent =
                    if (p0.isNullOrEmpty())
                        null
                    else p0.toString()


                val isNewContent = oldContent != p0

                getSearchList(viewModel.searchList.searchContent, isNewContent)
            }
            return false
        }

    }

    private fun getSearchList(searchContent: String?, newContent: Boolean) {
        val prefs = getSharedPreferences(Constants.Prefs.USER_TOKENS, MODE_PRIVATE)
        val accessToken = SharedPreferencesUtils.getAccessToken(prefs)

        if (!newContent && viewModel.searchList.bookListReachedOnTheEnd()) {
            showLongSnackBar(BookListViewModel.reachedOnTheEndErrorResponse().message)
        } else {
            binding.pgLoading.visibility = View.VISIBLE
            viewModel.searchListBorrow(
                accessToken,
                searchContent,
                bookId=bookId,
                newSearchContent = newContent,
                borrowStatusEnum = borrowStatus
            )
        }
    }

    private fun searchListListener() {
        viewModel.searchList.listLiveData.observe(this) {
            binding.pgLoading.visibility = View.GONE
            when (it) {
                is Result.Success -> {
                    borrowListAdapter.differ.submitList(it.response)
                    binding.pgLoading.visibility = View.GONE
                }
                is Result.Error -> {
                    when (it.errorBody.code) {
                        "reachedOnTheEnd" -> showLongSnackBar(getString(R.string.label_reached_in_the_end))
                        else -> showLongSnackBar(it.errorBody.message)
                    }
                }
            }
        }
    }

    private fun showLongSnackBar(message: String) {
        Snackbar.make(binding.root, message, BaseTransientBottomBar.LENGTH_LONG)
            .show()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search -> {
                searchViewName?.visibility = View.VISIBLE
                searchViewBook?.visibility = View.VISIBLE
            }
        }
        return true
    }


    override fun onItemCLick(position: Int) {
        val intent = Intent(this, EditBorrowActivity::class.java)
        intent.putExtra("borrowId", position)

        startActivity(intent)
        this.overridePendingTransition(
            androidx.transition.R.anim.abc_grow_fade_in_from_bottom,
            androidx.transition.R.anim.abc_fade_out
        )
    }
}