package com.trendyol.common

import android.content.res.Resources

fun dpToPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()