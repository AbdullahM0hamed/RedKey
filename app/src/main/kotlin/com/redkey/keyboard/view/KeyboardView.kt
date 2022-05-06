package com.redkey.keyboard.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.Button
import android.widget.GridLayout

class KeyboardView(
    val ctx: Context
) : GridLayout(ctx) {

    init {
        val button = Button(ctx)
        button.text = "Test"

        addView(button)
    }

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
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), (width / 2).toFloat(), Paint())
    }
}
