package com.bibliotecadebolso.app.util

import android.content.Context
import android.view.View
import android.widget.TextView
import com.bibliotecadebolso.app.R

object TextViewStatusUtils {

    fun showTvStatusMessage(tvErrorStatus: TextView, message: String) {
        tvErrorStatus.visibility = View.VISIBLE
        tvErrorStatus.text = message
    }

    fun clearTvStatus(tvErrorStatus: TextView) {
        tvErrorStatus.visibility = View.GONE
        tvErrorStatus.text = ""
    }

    fun showErrorOnTextView(context: Context, tvErrorStatus: TextView, error: Result.Error) {
        val text =
            if (error.errorBody.code == "noInternetConnection")
                context.getString(R.string.label_no_internet_connection)
            else error.errorBody.message

        tvErrorStatus.text = text
        tvErrorStatus.visibility = View.VISIBLE
    }
}