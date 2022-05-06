package com.redkey.keyboard.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import android.widget.Button
import android.widget.GridLayout

class KeyboardView(
    val ctx: Context
) : View(ctx) {

    //init {
        //val button = Button(ctx)
        //button.text = "Test"

        //addView(button)
    //}

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

        //canvas.drawRect(0f, 0f, width.toFloat(), (height / 5).toFloat(), Paint())
        //val paint = Paint()
        //paint.color = 0xFF00FF00.toInt()

        //canvas.drawText("1", 0, 1, (width / 2).toFloat(), 0f, paint)

        //canvas.save()
        val button = Button(ctx)
        button.text = "Test"
        button.draw(canvas)
        //canvas.restore()
    }
}
