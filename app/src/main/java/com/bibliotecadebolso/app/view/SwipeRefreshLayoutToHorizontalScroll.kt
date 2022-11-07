package com.bibliotecadebolso.app.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.math.abs


class SwipeRefreshLayoutToHorizontalScroll(context: Context, attrs: AttributeSet?) :
    SwipeRefreshLayout(context, attrs) {

    private var mTouchSlop = 0
    private var mPrevX = 0f

    init {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }


    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> mPrevX = MotionEvent.obtain(event).x
            MotionEvent.ACTION_MOVE -> {
                val eventX = event.x
                val xDiff = abs(eventX - mPrevX)
                if (xDiff > mTouchSlop) {
                    return false
                }
            }
        }
        return super.onInterceptTouchEvent(event)
    }
}