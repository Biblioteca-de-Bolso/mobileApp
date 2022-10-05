package com.bibliotecadebolso.app.ui.book.bookInfo.annotationList

import BookListDividerDecoration
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.app.AnnotationActionEnum
import com.bibliotecadebolso.app.databinding.ActivityAnnotationListBinding
import com.bibliotecadebolso.app.ui.adapter.AnnotationListAdapter
import com.bibliotecadebolso.app.ui.add.annotation.AnnotationEditorActivity
import com.bibliotecadebolso.app.util.*

class AnnotationListActivity : AppCompatActivity(), RvOnClickListener {
    private lateinit var binding: ActivityAnnotationListBinding
    private lateinit var fragmentAdapter: AnnotationListAdapter
    private lateinit var viewModel: AnnotationListViewModel
    private var bookId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ContextUtils.setActionBarColor(supportActionBar, this)
        supportActionBar?.title = getString(R.string.label_annotations)
        assignVariables()
        val accessToken = SharedPreferencesUtils.getAccessToken(
            getSharedPreferences(
                Constants.Prefs.USER_TOKENS,
                MODE_PRIVATE
            )
        )
        setupSwipeRefreshListener(accessToken)
        setupAnnotationListObserver()
        viewModel.getList(accessToken, bookId, 1)
        setupRecyclerView()
        setFabAddAnnotation()
        setContentView(binding.root)
    }


    private fun assignVariables() {
        binding = ActivityAnnotationListBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[AnnotationListViewModel::class.java]

        bookId = getIdFromExtrasOrMinus1()
    }


    private fun setupSwipeRefreshListener(accessToken: String) {
        binding.srAnnotationList.setOnRefreshListener {
            viewModel.getList(accessToken, bookId, 1)
        }
    }

    private fun setupAnnotationListObserver() {
        viewModel.liveDataAnnotationList.observe(this) {
            if (it is Result.Success) {
                fragmentAdapter.differ.submitList(it.response.annotations)
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
        }
    }

    private fun setFabAddAnnotation() {
        binding.fabShowAddOptions.setOnClickListener {
            val intent = Intent(this, AnnotationEditorActivity::class.java)
            intent.putExtra("actionType", AnnotationActionEnum.ADD.toString())
            intent.putExtra("bookId", bookId)
            startActivity(intent)
        }
    }


    override fun onItemCLick(position: Int) {
        val intent = Intent(this, AnnotationEditorActivity::class.java)
        intent.putExtra("annotationId", position)
        intent.putExtra("actionType", AnnotationActionEnum.EDIT.toString())
        startActivity(intent)
        // TODO("Not yet implemented")
    }
}