package com.bibliotecadebolso.app.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.Book
import com.bibliotecadebolso.app.data.model.CreatedBook
import com.bibliotecadebolso.app.databinding.ItemBookBinding
import com.bibliotecadebolso.app.databinding.ItemSearchBookTemplateBinding
import com.bibliotecadebolso.app.util.RvOnClickListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class BookSearchListAdapter(private var context: Context, private val rvOnClickListener: RvOnClickListener) :
    RecyclerView.Adapter<BookSearchListAdapter.BookViewHolder>() {

    /*
        BookViewHolder it's a template of each item.
     */
    inner class BookViewHolder(val binding: ItemSearchBookTemplateBinding, rvOnClickListener: RvOnClickListener) :
        RecyclerView.ViewHolder(binding.root) {
    }

    /*
        DifferCallBack is a list that search the elements more faster than a default kotlin List.
        You should use only on Adapters to accelerate screen refresh.
     */
    private val differCallBack = object : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }

    val differ: AsyncListDiffer<Book> = AsyncListDiffer(this, differCallBack)

    /*
        This method belows determines the creation and content of a ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchBookTemplateBinding.inflate(inflater)


        return BookViewHolder(binding, rvOnClickListener)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.binding.root.setOnClickListener{
            rvOnClickListener.onItemCLick(position)
        }

        val searchBook: Book = differ.currentList[position]

        holder.binding.tvTitle.text =
            if (searchBook.title.length <= 30) searchBook.title else searchBook.title.substring(0..30).plus("...")
        holder.binding.tvAuthor.text =
            if (searchBook.author.length <= 15) searchBook.author else searchBook.author.substring(0..15).plus("...")
        val imgView = holder.binding.ivBookDefault
        val linkThumbnail = searchBook.thumbnail.substring(0,4) + "s" + searchBook.thumbnail.substring(4)
        if (linkThumbnail.isNotEmpty()) {

            Glide.with(context).load(linkThumbnail)
                .centerCrop()
                .apply(RequestOptions().override(200, 280))
                .into(imgView)
        }


    }

    override fun getItemCount() = differ.currentList.size
}