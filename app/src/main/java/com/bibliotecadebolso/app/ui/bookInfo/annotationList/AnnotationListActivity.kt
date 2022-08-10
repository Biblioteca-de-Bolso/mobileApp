package com.bibliotecadebolso.app.ui.bookInfo.annotationList

import BookListDividerDecoration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.ActivityAnnotationListBinding
import com.bibliotecadebolso.app.ui.adapter.AnnotationListAdapter
import com.bibliotecadebolso.app.ui.adapter.BookListAdapter
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.RvOnClickListener
import com.bibliotecadebolso.app.util.SharedPreferencesUtils

class AnnotationListActivity : AppCompatActivity(), RvOnClickListener {
    private lateinit var binding: ActivityAnnotationListBinding
    private lateinit var fragmentAdapter: AnnotationListAdapter
    private lateinit var viewModel: AnnotationListViewModel
    private var bookId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnnotationListBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[AnnotationListViewModel::class.java]

        bookId = getIdFromExtrasOrMinus1()
        val accessToken = SharedPreferencesUtils.getAccessToken(
            getSharedPreferences(
                Constants.Prefs.USER_TOKENS,
                MODE_PRIVATE
            )
        )


        viewModel.getList(accessToken, bookId, 1)

        binding.srAnnotationList.setOnRefreshListener {
            viewModel.getList(accessToken, bookId, 1)
        }

        viewModel.liveDataAnnotationList.observe(this) {
            if (it is Result.Success) {
                fragmentAdapter.differ.submitList(it.response.annotations)
                fragmentAdapter.notifyDataSetChanged()
            }
            binding.srAnnotationList.isRefreshing = false
        }


        setupRecyclerView()
        setContentView(binding.root)
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
        }
    }

    private fun setupBookListObserver() {
        /*
        viewModel.bookList.observe(viewLifecycleOwner) {
            hideLoadingIcon()
            when (it) {
                is Result.Success<List<CreatedBook>> -> {

                    binding.srBookList.isRefreshing = false
                    fragmentAdapter.differ.submitList(it.response)
                    if (it.response.isEmpty()) {
                        //TODO make appear a text telling that doesn't have a book
                    }
                    fragmentAdapter.notifyDataSetChanged()
                }
                is Result.Error -> {
                    showLongSnackBar(it.errorBody.message)
                    binding.srBookList.isRefreshing = false
                }
            }

         */
    }


    override fun onItemCLick(position: Int) {
        Toast.makeText(this, getString(android.R.string.unknownName), Toast.LENGTH_LONG).show()
        // TODO("Not yet implemented")
    }
}