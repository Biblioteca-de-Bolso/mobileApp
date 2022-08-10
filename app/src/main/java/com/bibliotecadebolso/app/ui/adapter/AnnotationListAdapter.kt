package com.bibliotecadebolso.app.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.Annotation
import com.bibliotecadebolso.app.databinding.ItemBookBinding
import com.bibliotecadebolso.app.util.RvOnClickListener
import com.bumptech.glide.Glide

class AnnotationListAdapter(
    private var context: Context,
    private var rvOnClickListener: RvOnClickListener
) : RecyclerView.Adapter<AnnotationListAdapter.BookViewHolder>() {

    inner class BookViewHolder(val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<Annotation>() {
        override fun areItemsTheSame(oldItem: Annotation, newItem: Annotation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Annotation, newItem: Annotation): Boolean {
            return oldItem.id == newItem.id
        }
    }

    val differ: AsyncListDiffer<Annotation> = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AnnotationListAdapter.BookViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBookBinding.inflate(inflater)

        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnnotationListAdapter.BookViewHolder, position: Int) {
        val annotation: Annotation = differ.currentList[position]
        val defaultThumbnailInt = R.drawable.ic_item_book

        holder.binding.tvTitle.text = annotation.title
        holder.binding.tvAuthor.text = annotation.reference

        Glide.with(context)
            .load(defaultThumbnailInt)
            .centerInside()
            .into(holder.binding.ivBookDefault)

        holder.binding.root.setOnClickListener {
            rvOnClickListener.onItemCLick(annotation.id)
        }
    }

    override fun getItemCount() = differ.currentList.size
}