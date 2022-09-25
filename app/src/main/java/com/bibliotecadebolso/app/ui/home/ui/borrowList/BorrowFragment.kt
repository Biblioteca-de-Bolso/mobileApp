package com.bibliotecadebolso.app.ui.home.ui.borrowList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bibliotecadebolso.app.data.model.request.BookTitle
import com.bibliotecadebolso.app.data.model.request.Borrow
import com.bibliotecadebolso.app.data.model.request.BorrowStatus
import com.bibliotecadebolso.app.databinding.FragmentBorrowBinding
import com.bibliotecadebolso.app.ui.adapter.BorrowAdapter
import com.bibliotecadebolso.app.util.RvOnClickListener
import java.time.LocalDateTime

class BorrowFragment : Fragment(), RvOnClickListener {

    private lateinit var binding: FragmentBorrowBinding
    private lateinit var borrowAdapterPending: BorrowAdapter
    private lateinit var borrowAdapterReturned: BorrowAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentBorrowBinding.inflate(layoutInflater)

        borrowAdapterPending = BorrowAdapter(this.requireContext(), this)
        binding.rvListBorrowBorrowed.adapter = borrowAdapterPending
        binding.rvListBorrowBorrowed.layoutManager = LinearLayoutManager(this.requireContext())
        val list1: MutableList<Borrow> = mutableListOf()
        fillListWithOnlyPending(3,list1)

        borrowAdapterReturned = BorrowAdapter(this.requireContext(), this)
        binding.rvListBorrowReturned.adapter = borrowAdapterReturned
        binding.rvListBorrowReturned.layoutManager = LinearLayoutManager(this.requireContext())
        val list2: MutableList<Borrow> = mutableListOf()
        fillListWithOnlyReturned(3, list2)


        borrowAdapterPending.differ.submitList(list1)
        borrowAdapterReturned.differ.submitList(list2)
        return binding.root
    }

    private fun fillListWithOnlyReturned(times: Int, list: MutableList<Borrow>) {
        for (i in 1..times) {
            val now = LocalDateTime.now()
            val date =
                if (i % 2 == 0) now.plusDays(1).plusHours(1)
                else if (i % 3 == 0) now.plusWeeks(1).plusDays(1)
                else now.plusMonths(1).plusDays(1)

            val borrow = Borrow(i, i, i.toByte().times(4).toString(),BorrowStatus.RETURNED,date,null,now,now,
                BookTitle("teste")
            )
            list.add(
                borrow
            )
        }
    }

    fun fillListWithOnlyPending(times: Int, list: MutableList<Borrow>) {
        for (i in 1..times) {
            val status = BorrowStatus.PENDING
            val now = LocalDateTime.now()
            val date =
                if (i % 2 == 0) now.plusDays(1).plusHours(1)
                else if (i % 3 == 0) now.plusWeeks(1).plusDays(1)
                else now.plusMonths(1).plusDays(1)

            val borrow = Borrow(i, i, i.toByte().times(4).toString(),status,date,null,now,now,
                BookTitle("teste")
                )
            list.add(
                borrow
            )
        }
    }

    override fun onItemCLick(position: Int) {

    }
}