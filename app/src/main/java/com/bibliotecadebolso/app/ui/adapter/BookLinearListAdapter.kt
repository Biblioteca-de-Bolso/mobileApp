package com.bibliotecadebolso.app.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
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
    private var rvOnClickListener: RvOnClickListener
) : RecyclerView.Adapter<BookLinearListAdapter.BookViewHolder>() {

    inner class BookViewHolder(val binding: ItemBookBindingFullLengthBinding) :
        RecyclerView.ViewHolder(binding.root)


    private val differCallBack = object : DiffUtil.ItemCallback<CreatedBook>() {
        override fun areItemsTheSame(oldItem: CreatedBook, newItem: CreatedBook): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CreatedBook, newItem: CreatedBook): Boolean {
            return oldItem.id == newItem.id
        }
    }

    val differ: AsyncListDiffer<CreatedBook> = AsyncListDiffer(this, differCallBack)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):BookViewHolder {
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

        holder.binding.root.setOnClickListener { rvOnClickListener.onItemCLick(createdBook.id) }

    }


    override fun getItemCount(): Int = differ.currentList.size
}