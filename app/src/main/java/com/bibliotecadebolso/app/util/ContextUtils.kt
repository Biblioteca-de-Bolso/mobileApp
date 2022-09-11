package com.bibliotecadebolso.app.util

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import com.bibliotecadebolso.app.R

object ContextUtils {

    fun setActionBarColor(supportActionBar: ActionBar?, context: Context) {
        val colorDrawable = getActionBarColorDrawable(context)
        supportActionBar?.setBackgroundDrawable(colorDrawable)
    }

    private fun getActionBarColorDrawable(context: Context): ColorDrawable {
        return when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                ColorDrawable(
                    ContextCompat.getColor(context, R.color.purple_primary)
                )
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                ColorDrawable(
                    ContextCompat.getColor(context, R.color.white_blue)
                )
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                ColorDrawable(
                    ContextCompat.getColor(context, R.color.white_blue)
                )
            }
            else -> {
                ColorDrawable(
                    ContextCompat.getColor(context, R.color.white_blue)
                )
            }
        }
    }
}