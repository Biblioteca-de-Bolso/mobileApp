package com.bibliotecadebolso.app.data.model.app

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bibliotecadebolso.app.databinding.LayoutErrorHalfSizeBinding

class RecyclerViewContentManager(
    private val rvList: RecyclerView,
    private val ivShowMore: ImageView,
    private val errorBinding: LayoutErrorHalfSizeBinding
) {
    fun showContent() {
        hideErrorContent()
        rvList.visibility = View.VISIBLE
        ivShowMore.visibility = View.VISIBLE
    }

    fun showErrorContent(message: String) {
        hideContent()
        errorBinding.tvErrorTitle.text = message
        errorBinding.llErrorContent.visibility = View.VISIBLE
    }

    private fun hideErrorContent() {
        errorBinding.llErrorContent.visibility = View.GONE
    }

    private fun hideContent() {
        rvList.visibility = View.GONE
        ivShowMore.visibility = View.GONE
    }


}