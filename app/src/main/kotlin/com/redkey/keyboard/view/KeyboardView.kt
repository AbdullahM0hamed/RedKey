package com.redkey.keyboard.view

import android.content.Context
import android.graphics.Canvas
import android.view.View

class KeyboardView(
    val ctx: Context
) : View(ctx) {

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        setMeasuredDimension(575, 720)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(0xFFFF0000.toInt())
    }
}
