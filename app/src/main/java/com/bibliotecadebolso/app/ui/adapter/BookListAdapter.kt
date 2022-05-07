package com.bibliotecadebolso.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.databinding.ItemBookBinding

class BookListAdapter : RecyclerView.Adapter<BookListAdapter.BookViewHolder>(){

    /*
        BookViewHolder it's a template of each item.
     */
    inner class BookViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root) {
    }
    /*
        DifferCallBack is a list that search the elements more faster than a default kotlin List.
        You should use only on Adapters to accelerate screen refresh.
     */
    private val differCallBack = object : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.bookId == newItem.bookId
        }
        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.bookId == newItem.bookId
        }
    }

    val differ: AsyncListDiffer<Book> = AsyncListDiffer(this, differCallBack)

    /*
        This method belows determines the creation and content of a ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBookBinding.inflate(inflater)

        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book: Book = differ.currentList[position]

        holder.binding.tvTitle.text = book.title
        holder.binding.tvAuthor.text = book.author
    }

    override fun getItemCount() = differ.currentList.size
}