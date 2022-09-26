package com.bibliotecadebolso.app.ui.borrow.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.request.Borrow
import com.bibliotecadebolso.app.data.model.request.BorrowStatus
import com.bibliotecadebolso.app.databinding.ActivityBorrowListBinding
import com.bibliotecadebolso.app.ui.adapter.BorrowAdapter
import com.bibliotecadebolso.app.util.BorrowGeneratorUtils
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.ContextUtils
import com.bibliotecadebolso.app.util.RvOnClickListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BorrowListActivity : AppCompatActivity(), RvOnClickListener {

    private lateinit var binding: ActivityBorrowListBinding
    private lateinit var borrowListAdapter: BorrowAdapter
    var borrowStatus: BorrowStatus? = null

    private var searchViewName: SearchView? = null
    private var searchViewBook: SearchView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBorrowListBinding.inflate(layoutInflater)

        ContextUtils.setActionBarColor(supportActionBar, this)
        setCustomSearchSupportActionBar()
        initVariables()
        setupRecyclerView()
        fillRecyclerViewWithBorrowList()
        binding.pgLoading.visibility = View.GONE

        setCustomSearchListener()
        setContentView(binding.root)
    }

    private fun setCustomSearchSupportActionBar() {
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.custom_search_borrow, null)
        supportActionBar?.customView = view
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
    }

    private fun initVariables() {
        borrowStatus = intent.extras?.get("borrowStatus") as BorrowStatus

        val actionBarCustomView = actionBar?.customView
        searchViewName = actionBarCustomView?.findViewById(R.id.search1)
        searchViewBook = actionBarCustomView?.findViewById(R.id.search2)
    }

    private fun fillRecyclerViewWithBorrowList() {
        val list = mutableListOf<Borrow>()

        when (borrowStatus) {
            null -> BorrowGeneratorUtils.fillListWithOnlyReturned(10, list)
            BorrowStatus.PENDING -> BorrowGeneratorUtils.fillListWithOnlyPending(10, list)
            BorrowStatus.RETURNED -> BorrowGeneratorUtils.fillListWithOnlyReturned(10, list)
        }

        borrowListAdapter.differ.submitList(list)
    }

    private fun setupRecyclerView() {
        borrowListAdapter = BorrowAdapter(this, this)
        binding.rvListBorrow.adapter = borrowListAdapter
        binding.rvListBorrow.layoutManager = LinearLayoutManager(this)
    }

    private fun setCustomSearchListener() {
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
                val oldContent = ""
                val newContent =
                    if (p0.isNullOrEmpty()) null
                    else p0.toString()

                val isNewContent = oldContent != newContent
                // getSearchList(viewModel.searchList.searchContent, isNewContent)
            }
            return false
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_search -> {
                Log.e("menuSearch", "foi")
                searchViewName?.visibility = View.VISIBLE
                searchViewBook?.visibility = View.VISIBLE
            }
        }
        return true
    }


    override fun onItemCLick(position: Int) {
        TODO("Not yet implemented")
    }
}