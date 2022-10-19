package com.bibliotecadebolso.app.ui.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadebolso.app.databinding.ItemAnnotationFullWidthBinding
import com.bibliotecadebolso.app.util.RvOnClickListener

class AnnotationLinearListAdapter(
    private var context: Context,
    private var rvOnClickListener: RvOnClickListener
) :
    RecyclerView.Adapter<AnnotationLinearListAdapter.AnnotationViewHolder>() {

    inner class AnnotationViewHolder(val binding: ItemAnnotationFullWidthBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnotationViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: AnnotationViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}