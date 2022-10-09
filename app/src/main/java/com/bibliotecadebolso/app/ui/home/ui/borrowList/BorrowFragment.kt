package com.bibliotecadebolso.app.ui.home.ui.borrowList

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadebolso.app.R
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
import com.bibliotecadebolso.app.util.TextViewStatusUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class BorrowFragment : Fragment(), RvOnClickListener {

    private lateinit var binding: FragmentBorrowBinding
    private lateinit var borrowAdapterPending: BorrowAdapter
    private lateinit var borrowAdapterReturned: BorrowAdapter
    private lateinit var viewModel: BorrowViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentBorrowBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[BorrowViewModel::class.java]

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
        var tvErrorStatus = binding.tvErrorStatusRvReturned
        if (borrowStatus == BorrowStatus.RETURNED) {
            adapter = borrowAdapterReturned
            tvErrorStatus = binding.tvErrorStatusRvReturned
        }

        val observer = Observer<Result<List<Borrow>>> {
            when (it) {
                is Result.Success -> {
                    if (it.response.isEmpty()) {
                        TextViewStatusUtils.showTvStatusMessage(
                            tvErrorStatus,
                            getString(R.string.label_empty)
                        )
                        return@Observer
                    }
                    TextViewStatusUtils.clearTvStatus(tvErrorStatus)
                    showThreeItemsInRecyclerView(adapter, it.response)
                }
                is Result.Error -> {
                    TextViewStatusUtils.showErrorOnTextView(requireContext(), tvErrorStatus, it)
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
            startActivity(
                intent,
                ActivityOptions.makeSceneTransitionAnimation(this.requireActivity()).toBundle()
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
            startActivity(intent)
        }
    }

    private fun setFabAddBorrowClickListener() {
        binding.fabAddBorrow.setOnClickListener {
            val intent = Intent(this.requireContext(), AddBorrowActivity::class.java)
            startActivity(intent)
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

        startActivity(intent)
        this.requireActivity().overridePendingTransition(
            androidx.transition.R.anim.abc_grow_fade_in_from_bottom,
            androidx.transition.R.anim.abc_fade_out
        )
    }
}