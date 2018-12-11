package com.trendyol.common

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View

fun MotionEvent.isInViewRect(view: View, padding: Int, outRect: Rect): Boolean {
    val touchX = rawX.toInt()
    val touchY = rawY.toInt()
    view.getGlobalVisibleRect(outRect)
    outRect.addPadding(padding)
    return outRect.contains(touchX, touchY)
}