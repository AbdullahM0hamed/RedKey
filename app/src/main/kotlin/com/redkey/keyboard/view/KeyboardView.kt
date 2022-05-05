package com.redkey.keyboard.view

import android.content.Context
import android.graphics.Canvas
import android.widget.RelativeLayout

class KeyboardView(
    val ctx: Context
) : RelativeLayout(ctx) {

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        val width = ctx.resources.displayMetrics.widthPixels
        val height = ctx.resources.displayMetrics.heightPixels
        setMeasuredDimension(width, (0.45 * height).toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(0xFFFF0000.toInt())
    }
}
