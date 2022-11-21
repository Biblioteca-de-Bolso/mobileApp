package com.bibliotecadebolso.app.ui.book.bookInfo.annotationList

import BookListDividerDecoration
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.app.AnnotationActionEnum
import com.bibliotecadebolso.app.data.model.app.scroll.ScrollState
import com.bibliotecadebolso.app.databinding.ActivityAnnotationListBinding
import com.bibliotecadebolso.app.ui.ResultCodes
import com.bibliotecadebolso.app.ui.adapter.AnnotationListAdapter
import com.bibliotecadebolso.app.ui.add.annotation.AnnotationEditorActivity
import com.bibliotecadebolso.app.util.*

class AnnotationListActivity : AppCompatActivity(), RvOnClickListener {
    private lateinit var binding: ActivityAnnotationListBinding
    private lateinit var fragmentAdapter: AnnotationListAdapter
    private lateinit var viewModel: AnnotationListViewModel
    val scrollState: ScrollState = ScrollState()
    private var bookId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextUtils.setActionBarColor(supportActionBar, this)
        supportActionBar?.title = getString(R.string.label_annotations)
        assignVariables()
        setupSwipeRefreshListener()
        setupAnnotationListObserver()
        getSearchList()
        setupRecyclerView()
        setFabAddAnnotation()
        getAnnotationByIdObserver()
        setContentView(binding.root)
    }


    private fun assignVariables() {
        binding = ActivityAnnotationListBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[AnnotationListViewModel::class.java]

        bookId = getIdFromExtrasOrMinus1()
    }


    private fun setupSwipeRefreshListener() {
        binding.srAnnotationList.setOnRefreshListener {
            getSearchList()
        }
    }

    private fun setupAnnotationListObserver() {
        viewModel.searchList.listLiveData.observe(this) {
            if (it is Result.Success) {
                fragmentAdapter.differ.submitList(it.response)
                fragmentAdapter.notifyDataSetChanged()
            }
            binding.srAnnotationList.isRefreshing = false
        }

    }

    private fun getIdFromExtrasOrMinus1(): Int {
        val extras = intent.extras
        return extras?.getInt("bookId", -1) ?: -1
    }

    private fun setupRecyclerView() {
        fragmentAdapter = AnnotationListAdapter(this, this)
        val layoutManager = GridLayoutManager(this, 2)

        binding.rvListBook.apply {
            setLayoutManager(layoutManager)
            adapter = fragmentAdapter
            addItemDecoration(
                BookListDividerDecoration(
                    resources.getDimensionPixelSize(R.dimen.book_list_spacing),
                    resources.getInteger(R.integer.book_list_preview_columns)
                )
            )
            addOnScrollListener(listenerGetContentOnScrollOnBottom)
        }
    }

    private val listenerGetContentOnScrollOnBottom = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1)) {
                if (!scrollState.scrollOnBottom && !scrollState.isLoadingNewItems) {
                    scrollState.setAllBooleanAs(true)
                    getSearchList()
                }
            }
        }
    }

    private fun getSearchList() {
        val accessToken = SharedPreferencesUtils.getAccessToken(
            getSharedPreferences(
                Constants.Prefs.USER_TOKENS,
                MODE_PRIVATE
            )
        )
        viewModel.searchAnnotations(accessToken, bookId)
    }

    private fun setFabAddAnnotation() {
        binding.fabShowAddOptions.setOnClickListener {
            val intent = Intent(this, AnnotationEditorActivity::class.java)
            intent.putExtra("actionType", AnnotationActionEnum.ADD.toString())
            intent.putExtra("bookId", bookId)
            openAnnotationActivityResult.launch(intent)
        }
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
            viewModel.getAnnotationById(accessToken, id)
        }

        if (result.resultCode == ResultCodes.ANNOTATION_DELETED) {
            val id = result.data!!.extras!!.getInt("id")
            val indexAnnotation = fragmentAdapter.differ.currentList.indexOfFirst { it.id == id }
            if (indexAnnotation != -1) {
                val newList = fragmentAdapter.differ.currentList.toMutableList()
                newList.removeAt(indexAnnotation)
                fragmentAdapter.differ.submitList(newList)
            }
        }
    }

    private fun getAnnotationByIdObserver() {
        viewModel.liveDataAnnotationById.observe(this) {
            when (it) {
                is Result.Success -> {
                    val annotation = it.response.annotation
                    val annotationIndex = fragmentAdapter.differ.currentList.indexOfFirst { item -> item.id == annotation.id }

                    val updatedList = fragmentAdapter.differ.currentList.toMutableList()
                    if (annotationIndex == -1) {

                        updatedList.add(annotation)

                    } else {
                        updatedList[annotationIndex] = annotation
                    }

                    fragmentAdapter.differ.submitList(updatedList)
                }
                is Result.Error -> { }
            }
        }
    }
}