package com.bibliotecadebolso.app.ui.home.ui.borrowList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.ContentManager
import com.bibliotecadebolso.app.data.model.app.RecyclerViewContentManager
import com.bibliotecadebolso.app.data.model.request.Borrow
import com.bibliotecadebolso.app.data.model.request.BorrowStatus
import com.bibliotecadebolso.app.databinding.FragmentBorrowBinding
import com.bibliotecadebolso.app.ui.adapter.BorrowAdapter
import com.bibliotecadebolso.app.ui.borrow.add.AddBorrowActivity
import com.bibliotecadebolso.app.ui.borrow.edit.EditBorrowActivity
import com.bibliotecadebolso.app.ui.borrow.list.BorrowListActivity
import com.bibliotecadebolso.app.util.Constants
import com.bibliotecadebolso.app.util.Result
import com.bibliotecadebolso.app.util.RvOnClickListener
import kotlinx.coroutines.launch

class BorrowFragment : Fragment(), RvOnClickListener {

    private lateinit var binding: FragmentBorrowBinding
    private lateinit var borrowAdapterPending: BorrowAdapter
    private lateinit var borrowAdapterReturned: BorrowAdapter
    private lateinit var viewModel: BorrowViewModel
    private lateinit var borrowedContentManager: RecyclerViewContentManager
    private lateinit var returnedContentManager: RecyclerViewContentManager
    private lateinit var contentManager: ContentManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentBorrowBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[BorrowViewModel::class.java]

        setupContentManagers()
        setupRecyclerViewPending()
        setupRecyclerViewReturned()
        setRvObservers()

        setIvShowMoreBorrowedListener()
        setIvShowMoreReturnedListener()
        setFabAddBorrowClickListener()

        binding.swlRefreshHome.setOnRefreshListener {
            getBorrowPendingAndReturnedList()
        }

        lifecycleScope.launch {
            getBorrowPendingAndReturnedList()
        }

        return binding.root
    }

    private fun setupContentManagers() {
        borrowedContentManager = RecyclerViewContentManager(
            binding.rvListBorrowBorrowed,
            binding.ivViewMoreBorrowed,
            binding.includeLlBorrowedError
        )

        returnedContentManager = RecyclerViewContentManager(
            binding.rvListBorrowReturned,
            binding.ivViewMoreReturned,
            binding.includeLlReturnedError
        )

        contentManager = ContentManager(
            binding.llContent,
            binding.includeLlError
        )
    }

    private fun setupRecyclerViewPending() {
        borrowAdapterPending = BorrowAdapter(this.requireContext(), this)
        binding.rvListBorrowBorrowed.adapter = borrowAdapterPending
        binding.rvListBorrowBorrowed.layoutManager = LinearLayoutManager(this.requireContext())
    }

    private fun setupRecyclerViewReturned() {
        borrowAdapterReturned = BorrowAdapter(this.requireContext(), this)
        binding.rvListBorrowReturned.adapter = borrowAdapterReturned
        binding.rvListBorrowReturned.layoutManager = LinearLayoutManager(this.requireContext())
    }

    private fun setRvObservers() {
        viewModel.pendingBorrowListLiveData.observe(
            viewLifecycleOwner,
            getBorrowListObserver(BorrowStatus.PENDING)
        )
        viewModel.returnedBorrowListLiveData.observe(
            viewLifecycleOwner,
            getBorrowListObserver(BorrowStatus.RETURNED)
        )
    }

    private fun getBorrowListObserver(borrowStatus: BorrowStatus): Observer<Result<List<Borrow>>> {
        var adapter = borrowAdapterPending
        if (borrowStatus == BorrowStatus.RETURNED) {
            adapter = borrowAdapterReturned
        }

        val observer = Observer<Result<List<Borrow>>> {
            when (it) {
                is Result.Success -> {
                    binding.swlRefreshHome.isRefreshing = false
                    contentManager.showContent()
                    if (it.response.isEmpty()) {
                        val errorLabel = getString(R.string.label_empty)
                        when (borrowStatus) {
                            BorrowStatus.PENDING -> borrowedContentManager.showErrorContent(errorLabel)
                            BorrowStatus.RETURNED -> returnedContentManager.showErrorContent(errorLabel)
                        }
                        return@Observer
                    }

                    showThreeItemsInRecyclerView(adapter, it.response)
                    when (borrowStatus) {
                        BorrowStatus.PENDING -> borrowedContentManager.showContent()
                        BorrowStatus.RETURNED -> returnedContentManager.showContent()
                    }

                }
                is Result.Error -> {
                    contentManager.showErrorContent(it.errorBody.message)
                }
            }
            binding.swlRefreshHome.isRefreshing = false

        }
        return observer
    }

    private fun showThreeItemsInRecyclerView(adapter: BorrowAdapter, borrowList: List<Borrow>) {
        val lastIndex = if (borrowList.size <= 3) borrowList.size else 3
        adapter.differ.submitList(borrowList.subList(0, lastIndex))
    }

    private fun setIvShowMoreBorrowedListener() {
        binding.ivViewMoreBorrowed.setOnClickListener {
            val intent = Intent(this.requireContext(), BorrowListActivity::class.java)
            intent.putExtra("borrowStatus", BorrowStatus.PENDING)
            openBorrowOrOpenBorrowList.launch(
                intent,
                ActivityOptionsCompat.makeSceneTransitionAnimation(this.requireActivity())
            )
            this.requireActivity()
                .overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )
        }
    }


    private fun setIvShowMoreReturnedListener() {
        binding.ivViewMoreReturned.setOnClickListener {
            val intent = Intent(this.requireContext(), BorrowListActivity::class.java)
            intent.putExtra("borrowStatus", BorrowStatus.RETURNED)
            openBorrowOrOpenBorrowList.launch(intent)
        }
    }

    private fun setFabAddBorrowClickListener() {
        binding.fabAddBorrow.setOnClickListener {
            val intent = Intent(this.requireContext(), AddBorrowActivity::class.java)
            openBorrowOrOpenBorrowList.launch(intent)
            this.requireActivity().overridePendingTransition(
                androidx.transition.R.anim.abc_grow_fade_in_from_bottom,
                com.google.android.material.R.anim.abc_fade_out
            )
        }
    }

    private fun getBorrowPendingAndReturnedList() {
        val prefs = requireActivity().getSharedPreferences(
            Constants.Prefs.USER_TOKENS,
            AppCompatActivity.MODE_PRIVATE
        )
        val accessToken = prefs.getString(Constants.Prefs.Tokens.ACCESS_TOKEN, "")!!

        viewModel.getPendingBorrowList(accessToken)
        viewModel.getReturnedBorrowList(accessToken)
    }


    override fun onItemCLick(position: Int) {
        val intent = Intent(requireActivity(), EditBorrowActivity::class.java)
        intent.putExtra("borrowId", position)

        openBorrowOrOpenBorrowList.launch(intent)
        this.requireActivity().overridePendingTransition(
            androidx.transition.R.anim.abc_grow_fade_in_from_bottom,
            androidx.transition.R.anim.abc_fade_out
        )
    }

    private val openBorrowOrOpenBorrowList = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        getBorrowPendingAndReturnedList()
    }
}