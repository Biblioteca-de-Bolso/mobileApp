package com.bibliotecadebolso.app.ui.annotation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.app.AnnotationActionEnum
import com.bibliotecadebolso.app.data.model.app.scroll.ScrollState
import com.bibliotecadebolso.app.databinding.ActivityAnnotationList2Binding
import com.bibliotecadebolso.app.ui.ResultCodes
import com.bibliotecadebolso.app.ui.adapter.AnnotationLinearListAdapter
import com.bibliotecadebolso.app.ui.add.annotation.AnnotationEditorActivity
import com.bibliotecadebolso.app.ui.book.linearList.BookListViewModel
import com.bibliotecadebolso.app.util.*
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AnnotationLinearListActivity : AppCompatActivity(), RvOnClickListener {
    private lateinit var binding: ActivityAnnotationList2Binding
    private lateinit var annotationViewModel: AnnotationListViewModel
    val scrollState: ScrollState = ScrollState()

    val annotationListAdapter: AnnotationLinearListAdapter by lazy {
        AnnotationLinearListAdapter(this, this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnnotationList2Binding.inflate(layoutInflater)
        annotationViewModel = ViewModelProvider(this)[AnnotationListViewModel::class.java]

        ContextUtils.setActionBarColor(supportActionBar, this)

        setupRecyclerView()
        setupSearchListListener()
        getAnnotationByIdObserver()

        lifecycleScope.launch {
            delay(500L)
            getSearchList(null,true)
        }

        setContentView(binding.root)
    }

    private fun setupRecyclerView() {
        binding.rvListAnnotation.apply {
            adapter = annotationListAdapter
            val layoutManager = LinearLayoutManager(this@AnnotationLinearListActivity)
            setLayoutManager(layoutManager)
            addOnScrollListener(listenerGetContentOnScrollOnBottom)
        }
    }

    private val listenerGetContentOnScrollOnBottom = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1)) {
                if (!scrollState.scrollOnBottom && !scrollState.isLoadingNewItems) {
                    scrollState.setAllBooleanAs(true)
                    getSearchList(annotationViewModel.searchList.searchContent)
                }
            }
        }
    }

    private fun getSearchList(searchContent: String?, newSearchContent: Boolean = false) {
        val prefs = getSharedPreferences(
            Constants.Prefs.USER_TOKENS,
            MODE_PRIVATE
        )
        val accessToken = SharedPreferencesUtils.getAccessToken(prefs)

        if (!newSearchContent && annotationViewModel.searchList.bookListReachedOnTheEnd()) {
            showLongSnackBar(BookListViewModel.reachedOnTheEndErrorResponse().message)
        } else {

            binding.pgLoading.visibility = View.VISIBLE
            annotationViewModel.searchBook(
                accessToken,
                searchContent,
                newSearchContent = newSearchContent
            )
        }
    }

    private fun setupSearchListListener() {
        annotationViewModel.searchList.listLiveData.observe(this) {
            binding.pgLoading.visibility = View.GONE
            when (it) {
                is Result.Success -> {
                    annotationListAdapter.differ.submitList(it.response)
                }
                is Result.Error -> {
                    val message =
                        if (it.errorBody.code == "reachedOnTheEnd") getString(R.string.label_reached_in_the_end)
                        else it.errorBody.message
                    showLongSnackBar(message)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = getString(R.string.label_search)

        searchView.setQuery(annotationViewModel.searchList.searchContent, true)
        searchView.setOnSearchClickListener {
            annotationListAdapter.differ.submitList(emptyList())
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
                    val oldContent = annotationViewModel.searchList.searchContent
                    val newContent =
                        if (p0.isNullOrEmpty()) null
                        else p0.toString()

                    annotationViewModel.searchList.searchContent = newContent
                    val isNewContent = oldContent != newContent
                    getSearchList(annotationViewModel.searchList.searchContent, isNewContent)
                }
                return false
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun showLongSnackBar(message: String) {
        Snackbar.make(this, binding.root, message, BaseTransientBottomBar.LENGTH_LONG)
            .show()
    }

    override fun onItemCLick(position: Int) {
        val intent = Intent(this, AnnotationEditorActivity::class.java)
        intent.putExtra("annotationId", position)
        intent.putExtra("actionType", AnnotationActionEnum.EDIT.toString())
        openAnnotationActivityResult.launch(intent)
    }

    private val openAnnotationActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == ResultCodes.ANNOTATION_CREATED ||
                result.resultCode == ResultCodes.ANNOTATION_UPDATED) {
            val prefs = getSharedPreferences(
                Constants.Prefs.USER_TOKENS,
                MODE_PRIVATE
            )
            val accessToken = SharedPreferencesUtils.getAccessToken(prefs)

            val id = result.data!!.extras!!.getInt("id")
            annotationViewModel.getAnnotationById(accessToken, id)
        }

        if (result.resultCode == ResultCodes.ANNOTATION_DELETED) {
            val id = result.data!!.extras!!.getInt("id")
            val indexAnnotation = annotationListAdapter.differ.currentList.indexOfFirst { it.id == id }
            if (indexAnnotation != -1) {
                val newList = annotationListAdapter.differ.currentList.toMutableList()
                newList.removeAt(indexAnnotation)
                annotationListAdapter.differ.submitList(newList)
            }
        }
    }

    private fun getAnnotationByIdObserver() {
        annotationViewModel.liveDataAnnotationById.observe(this) {
            when (it) {
                is Result.Success -> {
                    val annotation = it.response.annotation
                    val annotationIndex = annotationListAdapter.differ.currentList.indexOfFirst { item -> item.id == annotation.id }

                    val updatedList = annotationListAdapter.differ.currentList.toMutableList()
                    if (annotationIndex == -1) {

                        updatedList.add(annotation)

                    } else {
                        updatedList[annotationIndex] = annotation
                    }

                    annotationListAdapter.differ.submitList(updatedList)
                }
                is Result.Error -> { }
            }
        }
    }
}