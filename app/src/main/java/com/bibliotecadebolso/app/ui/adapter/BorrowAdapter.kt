package com.bibliotecadebolso.app.ui.adapter

import android.content.Context
import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.CreatedBook
import com.bibliotecadebolso.app.data.model.request.Borrow
import com.bibliotecadebolso.app.data.model.request.BorrowStatus
import com.bibliotecadebolso.app.databinding.ItemBorrowBinding
import com.bibliotecadebolso.app.databinding.ItemBorrowPendingBinding
import com.bibliotecadebolso.app.databinding.ItemBorrowPendingNotPassedTimeBinding
import com.bibliotecadebolso.app.databinding.ItemBorrowPendingPassedTimeBinding
import com.bibliotecadebolso.app.util.RvOnClickListener
import java.time.Clock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date

class BorrowAdapter(
    private var context: Context,
    private var rvOnClickListener: RvOnClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_RETURNED = 1
        const val VIEW_PENDING_LATE = 2
        const val VIEW_PENDING_PASSED_TIME = 3
        const val VIEW_PENDING_NOT_PASSED_TIME = 4
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Borrow>() {
        override fun areItemsTheSame(oldItem: Borrow, newItem: Borrow): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Borrow, newItem: Borrow): Boolean {
            return oldItem.id == newItem.id
        }
    }

    val differ: AsyncListDiffer<Borrow> = AsyncListDiffer(this, differCallBack)

    override fun getItemViewType(position: Int): Int {
        val borrow = differ.currentList[position]
        if (borrow.BorrowStatus == BorrowStatus.RETURNED) {
            return VIEW_RETURNED
        } else {
            val dateTimeNow = LocalDateTime.now(Clock.systemUTC())

            return if (borrow.borrowDate >= dateTimeNow.plusMonths(1)) {
                VIEW_PENDING_LATE
            } else if (borrow.borrowDate >= dateTimeNow.plusWeeks(1)) {
                VIEW_PENDING_PASSED_TIME
            } else {
                VIEW_PENDING_NOT_PASSED_TIME
            }
        }
    }

    inner class BorrowViewHolderReturned(val binding: ItemBorrowBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class BorrowViewHolderPendingLate(val binding: ItemBorrowPendingBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class BorrowViewHolderPendingPassedTime(val binding: ItemBorrowPendingPassedTimeBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class BorrowViewHolderPendingNotPassedTIme(val binding: ItemBorrowPendingNotPassedTimeBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_RETURNED -> {
                val binding = ItemBorrowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return BorrowViewHolderReturned(
                    binding
                )
            }
            VIEW_PENDING_LATE -> BorrowViewHolderPendingLate(
                ItemBorrowPendingBinding.inflate(LayoutInflater.from(parent.context), parent, false)

            )
            VIEW_PENDING_PASSED_TIME -> BorrowViewHolderPendingPassedTime(
                ItemBorrowPendingPassedTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            VIEW_PENDING_NOT_PASSED_TIME -> BorrowViewHolderPendingNotPassedTIme(
                ItemBorrowPendingNotPassedTimeBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> BorrowViewHolderReturned(
                ItemBorrowBinding.inflate(LayoutInflater.from(context), parent, false)
            )

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val dateTimeNow = LocalDateTime.now(Clock.systemUTC())
        val borrow = differ.currentList[position]
        val dateMonthYearFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        when (holder) {
            is BorrowViewHolderReturned -> {
                holder.binding.apply {
                    tvBookTitle.text = borrow.book.title
                    tvBorrowDate.text = borrow.borrowDate.format(dateMonthYearFormatter)
                    tvBorrowerName.text = borrow.contactName
                }
            }
            is BorrowViewHolderPendingLate -> {
                holder.binding.apply {
                    tvBookTitle.text = borrow.book.title
                    tvBorrowDate.text = borrow.borrowDate.format(dateMonthYearFormatter)
                    tvBorrowerName.text = borrow.contactName
                    val timeUntilDevolution =
                        dateTimeNow.until(borrow.borrowDate, ChronoUnit.MONTHS)
                    tvTimeUntilDevolution.text =
                        context.getString(
                            if (timeUntilDevolution > 1) R.string.label_x_months_ago else R.string.label_x_month_ago,
                            timeUntilDevolution
                        )
                }
            }
            is BorrowViewHolderPendingPassedTime -> {
                holder.binding.apply {
                    tvBookTitle.text = borrow.book.title
                    tvBorrowDate.text = borrow.borrowDate.format(dateMonthYearFormatter)
                    tvBorrowerName.text = borrow.contactName
                    val timeUntilDevolution = dateTimeNow.until(borrow.borrowDate, ChronoUnit.WEEKS)
                    tvTimeUntilDevolution.text =
                        context.getString(
                            if (timeUntilDevolution > 1) R.string.label_x_weeks_ago else R.string.label_x_week_ago,
                            timeUntilDevolution
                        )
                }
            }
            is BorrowViewHolderPendingNotPassedTIme -> {
                holder.binding.apply {
                    tvBookTitle.text = borrow.book.title
                    tvBorrowDate.text = borrow.borrowDate.format(dateMonthYearFormatter)
                    tvBorrowerName.text = borrow.contactName
                    val timeUntilDevolution = dateTimeNow.until(borrow.borrowDate, ChronoUnit.DAYS)
                    tvTimeUntilDevolution.text =
                        context.getString(
                            if (timeUntilDevolution > 1) R.string.label_x_days_ago else R.string.label_x_day_ago,
                            timeUntilDevolution
                        )

                }
            }
        }

        holder.itemView.setOnClickListener {
        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}