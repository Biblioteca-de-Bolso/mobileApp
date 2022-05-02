package com.bibliotecadebolso.app.ui.home.ui.home

import BookListDividerDecoration
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.databinding.FragmentHomeBinding
import com.bibliotecadebolso.app.ui.adapter.BookListAdapter
import com.bibliotecadebolso.app.ui.add.book.AddBookActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var fragmentAdapter: BookListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        setupFabButtonListener()
        return root
    }

    private fun setupFabButtonListener() {
        binding.fabAddBook.setOnClickListener {
            val intent = Intent(requireContext(), AddBookActivity::class.java)
            startActivity(intent)

        }
    }

    private fun setupRecyclerView() {
        fragmentAdapter = BookListAdapter()
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
}