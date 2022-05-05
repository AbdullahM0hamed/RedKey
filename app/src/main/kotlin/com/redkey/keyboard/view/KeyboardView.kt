package com.redkey.keyboard.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.RelativeLayout

class KeyboardView(
    val ctx: Context
) : RelativeLayout(ctx) {

    val paint = Paint()

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
        paint.color = 0xFF000000.toInt()
        paint.strokeWidth = 1f
        canvas.drawColor(0xFFFF0000.toInt())
        canvas.drawRect(0f, 0f, width.toFloat(), (height / 5).toFloat(), paint)
    }
}
