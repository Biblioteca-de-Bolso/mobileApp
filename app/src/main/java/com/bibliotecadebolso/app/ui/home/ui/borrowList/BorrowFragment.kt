package com.bibliotecadebolso.app.ui.home.ui.borrowList

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadebolso.app.data.model.request.Borrow
import com.bibliotecadebolso.app.data.model.request.BorrowStatus
import com.bibliotecadebolso.app.databinding.FragmentBorrowBinding
import com.bibliotecadebolso.app.ui.adapter.BorrowAdapter
import com.bibliotecadebolso.app.ui.borrow.add.AddBorrowActivity
import com.bibliotecadebolso.app.ui.borrow.list.BorrowListActivity
import com.bibliotecadebolso.app.util.BorrowGeneratorUtils
import com.bibliotecadebolso.app.util.RvOnClickListener

class BorrowFragment : Fragment(), RvOnClickListener {

    private lateinit var binding: FragmentBorrowBinding
    private lateinit var borrowAdapterPending: BorrowAdapter
    private lateinit var borrowAdapterReturned: BorrowAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentBorrowBinding.inflate(layoutInflater)

        setupRecyclerViewPending()
        setupRecyclerViewReturned()
        fillRecyclerViewsWithBorrowGeneration()

        binding.ivViewMoreBorrowed.setOnClickListener {
            val intent = Intent(this.requireContext(), BorrowListActivity::class.java)
            intent.putExtra("borrowStatus", BorrowStatus.PENDING)
            startActivity(intent)
        }

        binding.ivViewMoreReturned.setOnClickListener {
            val intent = Intent(this.requireContext(), BorrowListActivity::class.java)
            intent.putExtra("borrowStatus", BorrowStatus.RETURNED)
            startActivity(intent)
        }

        binding.fabAddBorrow.setOnClickListener {
            val intent = Intent(this.requireContext(), AddBorrowActivity::class.java)
            startActivity(intent)
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

    private fun fillRecyclerViewsWithBorrowGeneration() {
        val list1: MutableList<Borrow> = mutableListOf()
        BorrowGeneratorUtils.fillListWithOnlyPending(3,list1)
        borrowAdapterPending.differ.submitList(list1)


        val list2: MutableList<Borrow> = mutableListOf()
        BorrowGeneratorUtils.fillListWithOnlyReturned(3, list2)
        borrowAdapterReturned.differ.submitList(list2)
    }


    override fun onItemCLick(position: Int) {

    }
}