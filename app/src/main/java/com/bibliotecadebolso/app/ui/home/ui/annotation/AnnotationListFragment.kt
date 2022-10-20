package com.bibliotecadebolso.app.ui.home.ui.annotation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.app.scroll.ScrollState
import com.bibliotecadebolso.app.databinding.FragmentAnnotationListBinding
import com.bibliotecadebolso.app.ui.adapter.AnnotationLinearListAdapter
import com.bibliotecadebolso.app.ui.book.linearList.BookListViewModel
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.RvOnClickListener
import com.bibliotecadebolso.app.util.SharedPreferencesUtils
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AnnotationListFragment : Fragment(), RvOnClickListener {

    private lateinit var viewModel: AnnotationListViewModel
    private lateinit var binding: FragmentAnnotationListBinding
    val scrollState: ScrollState = ScrollState()
    private val annotationListAdapter: AnnotationLinearListAdapter by lazy {
        AnnotationLinearListAdapter(
            requireContext(),
            this
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnnotationListBinding.inflate(inflater, container, false)
        initVariables()
        setupRecyclerView()
        setupSearchListListener()

        lifecycleScope.launch {
            delay(500L)
            getSearchList(null,true)
        }
        return binding.root
    }

    private fun initVariables() {
        viewModel = ViewModelProvider(this)[AnnotationListViewModel::class.java]
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())

        binding.rvListBook.apply {
            setLayoutManager(layoutManager)
            adapter = annotationListAdapter
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
        val prefs = requireContext().getSharedPreferences(
            Constants.Prefs.USER_TOKENS,
            AppCompatActivity.MODE_PRIVATE
        )
        val accessToken = SharedPreferencesUtils.getAccessToken(prefs)

        if (!newSearchContent && viewModel.searchList.bookListReachedOnTheEnd()) {
            showLongSnackBar(BookListViewModel.reachedOnTheEndErrorResponse().message)
        } else {

            binding.pgLoading.visibility = View.VISIBLE
            viewModel.searchBook(
                accessToken,
                searchContent,
                newSearchContent = newSearchContent
            )
        }
    }

    private fun setupSearchListListener() {
        viewModel.searchList.listLiveData.observe(viewLifecycleOwner) {
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

    private fun showLongSnackBar(message: String) {
        Snackbar.make(binding.rvListBook, message, BaseTransientBottomBar.LENGTH_LONG)
            .show()
    }


    override fun onItemCLick(position: Int) {
        Log.e("AnnotationListFragment", "clicked in an item")
    }


}