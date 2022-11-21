package com.bibliotecadebolso.app.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadebolso.app.R
import com.bibliotecadebolso.app.data.model.Annotation
import com.bibliotecadebolso.app.databinding.ItemAnnotationFullWidthBinding
import com.bibliotecadebolso.app.util.RvOnClickListener
import kotlin.math.pow

class AnnotationLinearListAdapter(
    private var context: Context,
    private var rvOnClickListener: RvOnClickListener
) :
    RecyclerView.Adapter<AnnotationLinearListAdapter.AnnotationViewHolder>() {

    inner class AnnotationViewHolder(val binding: ItemAnnotationFullWidthBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<Annotation>() {
        override fun areItemsTheSame(oldItem: Annotation, newItem: Annotation) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Annotation, newItem: Annotation) =
            oldItem.id == newItem.id
                    && oldItem.title == newItem.title
                    && oldItem.reference == newItem.reference

    }

    val differ: AsyncListDiffer<Annotation> = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnotationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAnnotationFullWidthBinding.inflate(inflater)

        return AnnotationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnnotationViewHolder, position: Int) {
        val annotation = differ.currentList[position]

        holder.binding.apply {
            val bookLabel = context.getString(R.string.label_book_unique)
            tvBookTitle.text = "$bookLabel : ${annotation.book!!.title}"
            tvAnnotationTitle.text = annotation.title
            tvReference.text = annotation.reference

            val rgbIntColor = annotation.bookId.toDouble().pow(4.0).toInt()


            Log.i("rgbIntColor", "Book: ${annotation.bookId} | color: $rgbIntColor")
            val hexColor = java.lang.String.format("#%06X", 0xFFFFFF and rgbIntColor)
            ivBookColor.backgroundTintList = ColorStateList.valueOf(Color.parseColor(hexColor))

            root.setOnClickListener {
                rvOnClickListener.onItemCLick(annotation.id)
            }

        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}