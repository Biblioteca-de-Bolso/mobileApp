package com.bibliotecadebolso.app.data.model

import android.view.View
import android.widget.LinearLayout
import com.bibliotecadebolso.app.databinding.LayoutErrorEmptyFullBinding

class ContentManager(
    private val content: LinearLayout,
    private val errorBinding: LayoutErrorEmptyFullBinding,
    private val imageId: Int? = null
) {
    fun showContent() {
        hideErrorContent()
        content.visibility = View.VISIBLE
    }
    private fun hideErrorContent() {
        errorBinding.llErrorContent.visibility = View.GONE
    }

    fun showErrorContent(message: String) {
        hideContent()
        if (imageId != null) errorBinding.ivError.setImageResource(imageId)
        errorBinding.tvErrorTitle.text = message
        errorBinding.llErrorContent.visibility = View.VISIBLE
    }

    private fun hideContent() {
        content.visibility = View.GONE
    }

}