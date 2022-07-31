package com.bibliotecadebolso.app.ui.home.ui.bookList

import BookListDividerDecoration
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.CreatedBook
import com.bibliotecadebolso.app.databinding.FragmentBookListBinding
import com.bibliotecadebolso.app.ui.adapter.BookListAdapter
import com.bibliotecadebolso.app.ui.add.book.AddBookActivity
import com.bibliotecadebolso.app.ui.bookInfo.BookInfoActivity
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.RvOnClickListener
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class BookListFragment : Fragment(), RvOnClickListener {

    private var _binding: FragmentBookListBinding? = null
    private val binding get() = _binding!!
    private lateinit var fragmentAdapter: BookListAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        setupBookListObserver()
        setupFabButtonListener()
        setupSwipeRefreshLayout()

        getList()
        return root
    }

    private fun setupSwipeRefreshLayout() {
        binding.srBookList.setOnRefreshListener {
            getList()
        }
    }

    private fun getList() {
        val prefs = requireActivity().getSharedPreferences(
            Constants.Prefs.USER_TOKENS,
            AppCompatActivity.MODE_PRIVATE,
        )

        val accessToken = prefs.getString(Constants.Prefs.Tokens.ACCESS_TOKEN, "")!!

        viewModel.apiListBook(accessToken)
    }

    private fun setupBookListObserver() {
        viewModel.bookList.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success<List<CreatedBook>> -> {
                    hideLoadingIcon()
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
        }
    }

    private fun showLongSnackBar(message: String) {
        Snackbar.make(binding.root, message, BaseTransientBottomBar.LENGTH_LONG)
            .show()
    }

    private fun setupFabButtonListener() {
        binding.fabAddBook.setOnClickListener {
            val intent = Intent(requireContext(), AddBookActivity::class.java)
            startActivity(intent)

        }
    }

    private fun setupRecyclerView() {
        fragmentAdapter = BookListAdapter(requireContext(), this)
        val layoutManager = GridLayoutManager(this.requireContext(), 2)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hideLoadingIcon(){ binding.pgLoading.visibility = View.GONE }
    private fun showLoadingIcon(){ binding.pgLoading.visibility = View.VISIBLE }


    override fun onItemCLick(position: Int) {
        val intent = Intent(context, BookInfoActivity::class.java)
        intent.putExtra("id", position)
        startActivity(intent)

        //val modalBottomSheet = BookItemBottomSheet(position)
        //modalBottomSheet.show(this.parentFragmentManager, BookItemBottomSheet.TAG)
    }
}