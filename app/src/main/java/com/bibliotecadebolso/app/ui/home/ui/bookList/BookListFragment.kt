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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.CreatedBook
import com.bibliotecadebolso.app.data.model.ReadStatusEnum
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
    private lateinit var fragmentAdapterPlanning: BookListAdapter
    private lateinit var fragmentAdapterReading: BookListAdapter
    private lateinit var fragmentAdapterConcluded: BookListAdapter
    private lateinit var fragmentAdapterDropped: BookListAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerViewPlanning()
        setupRecyclerViewReading()
        setupRecyclerViewConcluded()
        setupRecyclerViewDropped()
        setupBookListObserver()
        setupFabButtonListener()
        setupSwipeRefreshLayout()

        getList()
        return root
    }

    private fun setupSwipeRefreshLayout() {
        // binding.srBookList.setOnRefreshListener { getList() }
    }

    private fun getList() {
        val prefs = requireActivity().getSharedPreferences(
            Constants.Prefs.USER_TOKENS,
            AppCompatActivity.MODE_PRIVATE,
        )

        val accessToken = prefs.getString(Constants.Prefs.Tokens.ACCESS_TOKEN, "")!!
        showLoadingIcon()
        viewModel.apiListBook(accessToken, readStatusEnum = ReadStatusEnum.PLANNING)
        viewModel.apiListBook(accessToken, readStatusEnum = ReadStatusEnum.READING)
        viewModel.apiListBook(accessToken, readStatusEnum = ReadStatusEnum.CONCLUDED)
        viewModel.apiListBook(accessToken, readStatusEnum = ReadStatusEnum.DROPPED)

    }

    private fun setupBookListObserver() {
        viewModel.bookListPlanning.observe(viewLifecycleOwner) {
            hideLoadingIcon()
            loadBookList(it, ReadStatusEnum.PLANNING)
        }
        viewModel.bookListReading.observe(viewLifecycleOwner) {
            hideLoadingIcon()
            loadBookList(it, ReadStatusEnum.READING)
        }
        viewModel.bookListDropped.observe(viewLifecycleOwner) {
            hideLoadingIcon()
            loadBookList(it, ReadStatusEnum.DROPPED)
        }
        viewModel.bookListConcluded.observe(viewLifecycleOwner) {
            hideLoadingIcon()
            loadBookList(it, ReadStatusEnum.CONCLUDED)
        }
    }

    private fun loadBookList(result: Result<List<CreatedBook>>, readStatusEnum: ReadStatusEnum) {
        when (result) {
            is Result.Success<List<CreatedBook>> -> {
                // binding.srBookList.isRefreshing = false
                showBookListOnCorrectCategory(readStatusEnum, result.response)

            }
            is Result.Error -> {
                showLongSnackBar(result.errorBody.message)
                // binding.srBookList.isRefreshing = false
            }
        }
    }

    private fun showBookListOnCorrectCategory(readStatusEnum: ReadStatusEnum, list: List<CreatedBook>) {

        when (readStatusEnum) {
            ReadStatusEnum.PLANNING -> {
                if (list.isEmpty()) {
                    binding.rvListBookPlanning.visibility = View.GONE
                    binding.labelErrorPlanningToRead.visibility = View.VISIBLE
                }
                fragmentAdapterPlanning.differ.submitList(list)
                fragmentAdapterPlanning.notifyDataSetChanged()
            }
            ReadStatusEnum.READING -> {
                if (list.isEmpty()) {
                    binding.rvListBookReading.visibility = View.GONE
                    binding.labelErrorReading.visibility = View.VISIBLE
                }
                fragmentAdapterReading.differ.submitList(list)
                fragmentAdapterReading.notifyDataSetChanged()
            }
            ReadStatusEnum.DROPPED -> {
                if (list.isEmpty()) {
                    binding.rvListBookDropped.visibility = View.GONE
                    binding.labelErrorDropped.visibility = View.VISIBLE
                }
                fragmentAdapterDropped.differ.submitList(list)
                fragmentAdapterDropped.notifyDataSetChanged()
            }
            ReadStatusEnum.CONCLUDED -> {
                if (list.isEmpty()) {
                    binding.rvListBookConcluded.visibility = View.GONE
                    binding.labelErrorConcluded.visibility = View.VISIBLE
                }
                fragmentAdapterConcluded.differ.submitList(list)
                fragmentAdapterConcluded.notifyDataSetChanged()
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
            startActivityForResult(intent, ADD_BOOK)

        }
    }

    private fun setupRecyclerViewPlanning() {
        fragmentAdapterPlanning = BookListAdapter(requireContext(), this)
        val layoutManager = LinearLayoutManager(this.requireContext(), RecyclerView.HORIZONTAL, false)

        binding.rvListBookPlanning.apply {
            setLayoutManager(layoutManager)
            adapter = fragmentAdapterPlanning
        }
    }

    private fun setupRecyclerViewReading() {
        fragmentAdapterReading = BookListAdapter(requireContext(), this)
        val layoutManager = LinearLayoutManager(this.requireContext(), RecyclerView.HORIZONTAL, false)

        binding.rvListBookReading.apply {
            setLayoutManager(layoutManager)
            adapter = fragmentAdapterReading
        }
    }
    private fun setupRecyclerViewConcluded() {
        fragmentAdapterConcluded = BookListAdapter(requireContext(), this)
        val layoutManager = LinearLayoutManager(this.requireContext(), RecyclerView.HORIZONTAL, false)

        binding.rvListBookConcluded.apply {
            setLayoutManager(layoutManager)
            adapter = fragmentAdapterConcluded
        }
    }

    private fun setupRecyclerViewDropped() {
        fragmentAdapterDropped = BookListAdapter(requireContext(), this)
        val layoutManager = LinearLayoutManager(this.requireContext(), RecyclerView.HORIZONTAL, false)

        binding.rvListBookDropped.apply {
            setLayoutManager(layoutManager)
            adapter = fragmentAdapterDropped
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
        startActivityForResult(intent, VIEW_DETAIL_BOOK)

        //val modalBottomSheet = BookItemBottomSheet(position)
        //modalBottomSheet.show(this.parentFragmentManager, BookItemBottomSheet.TAG)
    }

    companion object {
        const val VIEW_DETAIL_BOOK = 30
        const val ADD_BOOK = 20
        const val BOOK_ADDED = 21
        const val REMOVE_BOOK = 31
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == VIEW_DETAIL_BOOK && resultCode == REMOVE_BOOK)
            getList()
        else if (requestCode == ADD_BOOK && resultCode == BOOK_ADDED)
            getList()

    }
}