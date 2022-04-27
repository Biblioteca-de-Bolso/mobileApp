package com.bibliotecadebolso.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadebolso.app.databinding.ItemBookBinding

class BookListAdapter : RecyclerView.Adapter<BookListAdapter.BookViewHolder>(){

    private val list = mutableListOf<String>(
        "the great mage returned after 4000 years to combat",
        "A man seach for meaning",
        "Tio Bob - CLean code",
        "The beginning after the end",
        "the great mage returned after 4000 years to combat",
        "A man seach for meaning",
        "Tio Bob - CLean code"
    )
    inner class BookViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBookBinding.inflate(inflater)

        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.binding.tvTitle.text = list[position]
        holder.binding.tvAuthor.text = "By Author"
    }

    override fun getItemCount() = list.size
}