package com.redkey.keyboard.view

import android.content.Context
import android.view.View

class KeyboardView(
    val ctx: Context
) : View(context) {

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        setMeasuredDimension(100, 100)
    }
}
