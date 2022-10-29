package com.bibliotecadebolso.app.ui.home.ui.annotation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.ContentManager
import com.bibliotecadebolso.app.data.model.app.AnnotationActionEnum
import com.bibliotecadebolso.app.data.model.app.scroll.ScrollState
import com.bibliotecadebolso.app.databinding.FragmentAnnotationListBinding
import com.bibliotecadebolso.app.ui.adapter.AnnotationLinearListAdapter
import com.bibliotecadebolso.app.ui.add.annotation.AnnotationEditorActivity
import com.bibliotecadebolso.app.ui.annotation.AnnotationLinearListActivity
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
    private lateinit var contentManager: ContentManager
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
        contentManager = ContentManager(binding.llContent, binding.includeLlError, R.drawable.ic_annotation)
        initVariables()
        setupRecyclerView()
        setupSearchListListener()


        binding.ivViewMoreNotes.setOnClickListener {
            val intent = Intent(requireContext(), AnnotationLinearListActivity::class.java)
            startActivity(intent)

        }
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
                    if (it.response.isEmpty()) {
                        contentManager.showErrorContent(getString(R.string.label_annotation_list_is_empty))
                    } else {
                        contentManager.showContent()
                    }
                    annotationListAdapter.differ.submitList(it.response)
                }
                is Result.Error -> {
                    val message =
                        if (it.errorBody.code == "reachedOnTheEnd") getString(R.string.label_reached_in_the_end)
                        else it.errorBody.message
                    contentManager.showErrorContent(message)
                }
            }
        }
    }



    private fun showLongSnackBar(message: String) {
        Snackbar.make(binding.rvListBook, message, BaseTransientBottomBar.LENGTH_LONG)
            .show()
    }


    override fun onItemCLick(position: Int) {
        val intent = Intent(requireContext(), AnnotationEditorActivity::class.java)
        intent.putExtra("annotationId", position)
        intent.putExtra("actionType", AnnotationActionEnum.EDIT.toString())
        startActivity(intent)
    }


}