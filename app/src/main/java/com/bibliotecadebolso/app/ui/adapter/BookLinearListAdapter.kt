package com.bibliotecadebolso.app.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.CreatedBook
import com.bibliotecadebolso.app.databinding.ItemBookBindingFullLengthBinding
import com.bibliotecadebolso.app.util.RvOnClickListener
import com.bumptech.glide.Glide

class BookLinearListAdapter(
    private var context: Context,
    private var rvOnClickListener: RvOnClickListener,
    private var toShowBorrowStatus: Boolean = false,
    private var toCancelClickIfCurrentlyBorrowed: Boolean = false,
) : RecyclerView.Adapter<BookLinearListAdapter.BookViewHolder>() {

    inner class BookViewHolder(val binding: ItemBookBindingFullLengthBinding) :
        RecyclerView.ViewHolder(binding.root)


    private val differCallBack = object : DiffUtil.ItemCallback<CreatedBook>() {
        override fun areItemsTheSame(oldItem: CreatedBook, newItem: CreatedBook): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CreatedBook, newItem: CreatedBook): Boolean {
            return oldItem.equals(newItem)
        }
    }

    val differ: AsyncListDiffer<CreatedBook> = AsyncListDiffer(this, differCallBack)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBookBindingFullLengthBinding.inflate(inflater)

        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val createdBook = differ.currentList[position]
        val defaultThumbnailInt = R.drawable.ic_item_book

        holder.binding.tvTitle.text = createdBook.title
        holder.binding.tvAuthor.text = createdBook.author
        holder.binding.tvIsbn10.text = "ISBN-10: ${createdBook.isbn10}"
        holder.binding.tvIsbn13.text = "ISBN-13: ${createdBook.isbn13}"

        Glide.with(context)
            .load(createdBook.thumbnail.ifEmpty { defaultThumbnailInt })
            .centerInside()
            .into(holder.binding.ivThumbnail)

        val currentlyBorrowed = createdBook.currentlyBorrowed

        if (!currentlyBorrowed) {
            resetBorrowedStatusLayout(holder)
        } else {
            if (toShowBorrowStatus)
                setTextAsBorrowed(holder.binding.tvBorrowStatus)

            if (toCancelClickIfCurrentlyBorrowed)
                setContentHalfTransparent(holder.binding.llContent)
        }

        holder.binding.root.setOnClickListener {
            if (toCancelClickIfCurrentlyBorrowed && currentlyBorrowed) {
                showShortToast(context.getString(R.string.book_already_borrowed_select_other))
                return@setOnClickListener
            }

            rvOnClickListener.onItemCLick(createdBook.id)
        }

    }

    private fun resetBorrowedStatusLayout(holder: BookLinearListAdapter.BookViewHolder) {
        holder.binding.tvBorrowStatus.text = ""
        holder.binding.llContent.alpha = 1.0f

    }

    private fun setTextAsBorrowed(tvBorrowStatus: TextView) {
        tvBorrowStatus.text = context.getString(R.string.label_borrowed).uppercase()
    }

    private fun setContentHalfTransparent(llContent: LinearLayout) {
        val fiftyPercentTransparent = 0.5f
        llContent.alpha = fiftyPercentTransparent
    }

    private fun showShortToast(string: String) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
    }


    override fun getItemCount(): Int = differ.currentList.size
}