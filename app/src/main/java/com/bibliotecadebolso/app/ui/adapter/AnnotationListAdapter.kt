package com.bibliotecadebolso.app.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.Annotation
import com.bibliotecadebolso.app.databinding.ItemAnnotationBinding
import com.bibliotecadebolso.app.databinding.ItemBookBinding
import com.bibliotecadebolso.app.util.RvOnClickListener
import com.bumptech.glide.Glide

class AnnotationListAdapter(
    private var context: Context,
    private var rvOnClickListener: RvOnClickListener
) : RecyclerView.Adapter<AnnotationListAdapter.AnnotationViewHolder>() {

    inner class AnnotationViewHolder(val binding: ItemAnnotationBinding) : RecyclerView.ViewHolder(binding.root)

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
    ): AnnotationListAdapter.AnnotationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAnnotationBinding.inflate(inflater)

        return AnnotationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnnotationListAdapter.AnnotationViewHolder, position: Int) {
        val annotation: Annotation = differ.currentList[position]
        holder.binding.tvTitle.text = annotation.title
        holder.binding.tvReference.text = annotation.reference

        holder.binding.root.setOnClickListener {
            rvOnClickListener.onItemCLick(annotation.id)
        }
    }

    override fun getItemCount() = differ.currentList.size
}