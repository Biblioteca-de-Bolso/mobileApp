package com.bibliotecadebolso.app.ui.home.ui.bookList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.ContentManager
import com.bibliotecadebolso.app.data.model.CreatedBook
import com.bibliotecadebolso.app.data.model.ReadStatusEnum
import com.bibliotecadebolso.app.databinding.FragmentBookListBinding
import com.bibliotecadebolso.app.ui.ResultCodes
import com.bibliotecadebolso.app.ui.adapter.BookListAdapter
import com.bibliotecadebolso.app.ui.add.book.AddBookActivity
import com.bibliotecadebolso.app.ui.book.linearList.BookListActivity
import com.bibliotecadebolso.app.ui.book.bookInfo.BookInfoActivity
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
    private lateinit var contentManager: ContentManager
    private var getListCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        contentManager =
            ContentManager(binding.llContent, binding.includeLlError, R.drawable.ic_error)

        setupRecyclerViewPlanning()
        setupRecyclerViewReading()
        setupRecyclerViewConcluded()
        setupRecyclerViewDropped()
        setupOnClickShowMoreListener()
        setupBookListObserver()
        setupFabButtonListener()

        binding.swlRefreshHome.setOnRefreshListener {
            getListCount = 4
            getList()
        }

        getList()
        return root
    }

    private fun setupRecyclerViewPlanning() {
        fragmentAdapterPlanning = BookListAdapter(requireContext(), this)
        setupBookListRecyclerView(fragmentAdapterPlanning, binding.rvListBookPlanning)
    }

    private fun setupRecyclerViewReading() {
        fragmentAdapterReading = BookListAdapter(requireContext(), this)
        setupBookListRecyclerView(fragmentAdapterReading, binding.rvListBookReading)
    }

    private fun setupRecyclerViewConcluded() {
        fragmentAdapterConcluded = BookListAdapter(requireContext(), this)
        setupBookListRecyclerView(fragmentAdapterConcluded, binding.rvListBookConcluded)
    }

    private fun setupRecyclerViewDropped() {
        fragmentAdapterDropped = BookListAdapter(requireContext(), this)
        setupBookListRecyclerView(fragmentAdapterDropped, binding.rvListBookDropped)
    }

    private fun setupBookListRecyclerView(
        fragmentAdapter: BookListAdapter,
        recyclerView: RecyclerView
    ) {
        val layoutManager =
            LinearLayoutManager(this.requireContext(), RecyclerView.HORIZONTAL, false)

        recyclerView.apply {
            setLayoutManager(layoutManager)
            adapter = fragmentAdapter
        }
    }

    private fun setupOnClickShowMoreListener() {

        binding.ivViewMorePlanning.setOnClickListener {
            startBookListActivity(enumChoosed = ReadStatusEnum.PLANNING)
        }
        binding.ivViewMoreReading.setOnClickListener {
            startBookListActivity(enumChoosed = ReadStatusEnum.READING)
        }
        binding.ivViewMoreConcluded.setOnClickListener {
            startBookListActivity(enumChoosed = ReadStatusEnum.CONCLUDED)
        }
        binding.ivViewMoreDropped.setOnClickListener {
            startBookListActivity(enumChoosed = ReadStatusEnum.DROPPED)
        }
    }

    private fun startBookListActivity(enumChoosed: ReadStatusEnum) {
        val intent = Intent(requireContext(), BookListActivity::class.java)
        intent.putExtra("readStatusEnum", enumChoosed.toString())
        startActivity(intent)
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
        reduceListCountAndSetRefreshingAsFalse()
        when (result) {
            is Result.Success<List<CreatedBook>> -> {
                contentManager.showContent()
                showBookListOnCorrectCategory(readStatusEnum, result.response)
            }
            is Result.Error -> {
                contentManager.showErrorContent(result.errorBody.message)
                showLongSnackBar(result.errorBody.message)
            }
        }
    }

    private fun reduceListCountAndSetRefreshingAsFalse() {
        getListCount--
        if (getListCount == 0) binding.swlRefreshHome.isRefreshing = false
    }

    private fun showBookListOnCorrectCategory(
        readStatusEnum: ReadStatusEnum,
        list: List<CreatedBook>
    ) {
        lateinit var fragmentAdapter: BookListAdapter
        var rvListBook: RecyclerView? = null
        var labelError: TextView? = null
        lateinit var showMoreArrow: ImageView

        when (readStatusEnum) {
            ReadStatusEnum.PLANNING -> {
                labelError = binding.labelErrorPlanningToRead
                rvListBook = binding.rvListBookPlanning
                showMoreArrow = binding.ivViewMorePlanning
                fragmentAdapter = fragmentAdapterPlanning
            }
            ReadStatusEnum.READING -> {
                labelError = binding.labelErrorReading
                rvListBook = binding.rvListBookReading
                showMoreArrow = binding.ivViewMoreReading
                fragmentAdapter = fragmentAdapterReading
            }
            ReadStatusEnum.DROPPED -> {
                labelError = binding.labelErrorDropped
                rvListBook = binding.rvListBookDropped
                showMoreArrow = binding.ivViewMoreDropped
                fragmentAdapter = fragmentAdapterDropped
            }
            ReadStatusEnum.CONCLUDED -> {
                labelError = binding.labelErrorConcluded
                rvListBook = binding.rvListBookConcluded
                showMoreArrow = binding.ivViewMoreConcluded
                fragmentAdapter = fragmentAdapterConcluded
            }
        }

        showBookList(fragmentAdapter, list)
        showErrorMessageIfListIsEmpty(list, rvListBook, labelError)
        hideArrowIfListHasLessThan5OrShow(list, showMoreArrow)
    }

    private fun showBookList(fragmentAdapter: BookListAdapter, list: List<CreatedBook>) {
        fragmentAdapter.differ.submitList(list)
        fragmentAdapter.notifyDataSetChanged()
    }

    private fun showErrorMessageIfListIsEmpty(
        list: List<CreatedBook>,
        rvListBook: RecyclerView?,
        labelError: TextView?
    ) {
        if (list.isEmpty()) {
            rvListBook?.visibility = View.GONE
            labelError?.visibility = View.VISIBLE
        } else {
            rvListBook?.visibility = View.VISIBLE
            labelError?.visibility = View.GONE
        }
    }

    private fun hideArrowIfListHasLessThan5OrShow(
        list: List<CreatedBook>,
        showMoreArrow: ImageView
    ) {
        showMoreArrow.visibility = if (list.size <= 5) View.GONE else View.VISIBLE
    }

    private fun showLongSnackBar(message: String) {
        Snackbar.make(binding.root, message, BaseTransientBottomBar.LENGTH_LONG)
            .show()
    }

    private fun setupFabButtonListener() {
        binding.fabAddBook.setOnClickListener {
            val intent = Intent(requireContext(), AddBookActivity::class.java)

            addBookActivityResult.launch(intent)

        }
    }

    private val addBookActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.e("BookListFragment", "Entrou no addBookActivityResult")
        if (result.resultCode == ResultCodes.BOOK_ADDED) {
            Log.e("BookListFragment", "ResultCode == ResultCodes.BOOK_ADDED")
            getList()
        } else {
            Log.e("BookListFragment", "${result.resultCode == ResultCodes.BOOK_ADDED}")
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hideLoadingIcon() {
        binding.pgLoading.visibility = View.GONE
    }

    private fun showLoadingIcon() {
        binding.pgLoading.visibility = View.VISIBLE
    }


    override fun onItemCLick(position: Int) {
        val intent = Intent(context, BookInfoActivity::class.java)
        intent.putExtra("id", position)
        bookInfoActivityResult.launch(intent)
    }

    private val bookInfoActivityResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == ResultCodes.BOOK_REMOVED) {

            val readStatusEnum = ReadStatusEnum.valueOf(
                result.data!!.extras!!.getString(
                    "readStatusEnum",
                    "PLANNING"
                )
            )
            val adapter = when (readStatusEnum) {
                ReadStatusEnum.PLANNING -> fragmentAdapterPlanning
                ReadStatusEnum.READING -> fragmentAdapterReading
                ReadStatusEnum.CONCLUDED -> fragmentAdapterConcluded
                ReadStatusEnum.DROPPED -> fragmentAdapterDropped
            }
            val list = adapter.differ.currentList.toMutableList()

            val book = list.find { it.id == result.data!!.extras!!.getInt("id") }
            val index = list.indexOfFirst { it.id == result.data!!.extras!!.getInt("id") }

            Log.e("BookListFragment", "book != null : ${index!=-1}")
            if (index != -1) list.removeAt(index)
            showBookListOnCorrectCategory(readStatusEnum, list)
        } else if (setOf(
            ResultCodes.BOOK_EDITED,
            ResultCodes.BOOK_REMOVED,
            ResultCodes.BOOK_UPDATED_STATUS,
            ResultCodes.BOOK_EDITED_AND_UPDATED_STATUS,
        ).contains(result.resultCode))
            getList()
    }

    companion object {
        const val VIEW_DETAIL_BOOK = 30
        const val ADD_BOOK = 20
        const val BOOK_ADDED = 21
        const val REMOVE_BOOK = 31
        const val UPDATED_BOOK = 32
        const val UPDATED_STATUS = 33
        const val UPDATED_BOOK_AND_STATUS = 34
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (setOf(
                ResultCodes.BOOK_EDITED,
                ResultCodes.BOOK_REMOVED,
                ResultCodes.BOOK_UPDATED_STATUS,
                ResultCodes.BOOK_EDITED_AND_UPDATED_STATUS,
            ).contains(resultCode)
        )
            getList()
        else if (resultCode == BOOK_ADDED)
            getList()

    }
}