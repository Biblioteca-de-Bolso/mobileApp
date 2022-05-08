package com.bibliotecadebolso.app.ui.add.book

import BookListDividerDecoration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.FragmentAddBookOptionsBinding
import com.bibliotecadebolso.app.ui.adapter.BookListAdapter
import com.bibliotecadebolso.app.ui.adapter.BookSearchListAdapter
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.RvOnClickListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddBookOptionsFragment : Fragment(), RvOnClickListener {

    private var _binding: FragmentAddBookOptionsBinding? = null
    private val viewModel: AddBookViewModel by activityViewModels()
    private val binding get() = _binding!!
    private lateinit var fragmentAdapter: BookSearchListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBookOptionsBinding.inflate(inflater, container, false)
        binding.btnAddOwnBook.setOnClickListener {

            findNavController().navigate(R.id.action_optionsFragment_to_AddOfflineBookFragment)
        }

        viewModel.booksSearchContent.observe(viewLifecycleOwner) {
            if (it is Result.Success) {
                fragmentAdapter.differ.submitList(it.response)
                fragmentAdapter.notifyDataSetChanged()
            } else {
                val errorResult = it as Result.Error
                Toast.makeText(
                    this.requireContext(),
                    errorResult.errorBody.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        setupRecyclerView()

        val etSearchBook = binding.etSearchBook.editText!!

        var job: Job? = null
        etSearchBook.addTextChangedListener { editableText ->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_NEWS_DELAY)
                editableText?.let {
                    if (editableText.toString().isNotEmpty()) {
                        val prefs = requireActivity().getSharedPreferences(
                            Constants.Prefs.USER_TOKENS,
                            AppCompatActivity.MODE_PRIVATE
                        )
                        val accessToken = prefs.getString(Constants.Prefs.Tokens.ACCESS_TOKEN, "")!!
                        viewModel.apiListBook(accessToken, editableText.toString())
                    }
                }
            }
        }

        return binding.root
    }


    private fun setupRecyclerView() {
        fragmentAdapter = BookSearchListAdapter(requireContext(), this)
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

    override fun onItemCLick(position: Int) {
        val book = (viewModel.booksSearchContent.value as Result.Success).response[position]

        findNavController().navigate(
            R.id.action_optionsFragment_to_AddOfflineBookFragment,
            Bundle().also { it.putParcelable("book", book) })
    }
}
