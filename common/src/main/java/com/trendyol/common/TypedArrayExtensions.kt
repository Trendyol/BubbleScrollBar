package com.trendyol.common

import android.content.res.TypedArray

fun TypedArray.getDimensionOrDefaultInPixelSize(attr: Int, defaultResource: Int)
        = getDimensionPixelSize(attr, resources.getDimensionPixelSize(defaultResource))