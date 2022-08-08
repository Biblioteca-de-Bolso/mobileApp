package com.bibliotecadebolso.app.ui.bookInfo.annotationList

import BookListDividerDecoration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.CreatedBook
import com.bibliotecadebolso.app.databinding.ActivityAnnotationListBinding
import com.bibliotecadebolso.app.ui.adapter.BookListAdapter
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.RvOnClickListener

class AnnotationListActivity : AppCompatActivity(), RvOnClickListener {
    private lateinit var binding: ActivityAnnotationListBinding
    private lateinit var fragmentAdapter: BookListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnnotationListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bookId = getIdFromExtrasOrMinus1()
        setupRecyclerView()
    }

    private fun getIdFromExtrasOrMinus1(): Int {
        val extras = intent.extras
        return extras?.getInt("id", -1) ?: -1
    }

    private fun setupRecyclerView() {
        fragmentAdapter = BookListAdapter(this, this)
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
        TODO("Not yet implemented")
    }
}