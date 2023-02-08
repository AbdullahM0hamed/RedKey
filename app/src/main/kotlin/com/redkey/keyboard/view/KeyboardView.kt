package com.redkey.keyboard.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.GridLayout

class KeyboardView(
    val ctx: Context,
    val connection: InputConnection,
    val keys: List<String>
) : ViewGroup(ctx) {

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        if (e?.action == MotionEvent.ACTION_DOWN) {
            keys.forEachIndexed { i, key ->
                if (e.x <= (width / keys.size) * (i + 1) && e.x > (width / keys.size) * i) {
                    connection.commitText(key, 1)
                }
            }
        }

        return false
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        val width = ctx.resources.displayMetrics.widthPixels
        val height = ctx.resources.displayMetrics.heightPixels
        setMeasuredDimension(width, (0.45 * height).toInt())
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawColor(0xFF000000.toInt())

        val paint = Paint()
        paint.color = 0xFFFF0000.toInt()
        val margin = 10f
        paint.color = 0x66888888
        val paint2 = Paint()
        paint2.color = 0xFFFF0000.toInt()
        paint2.textSize = 24f
        keys.forEachIndexed { i, key ->
            val rect = RectF(((canvas.width / keys.size) * i).toFloat(), margin, ((canvas.width / keys.size) * (i + 1)).toFloat() - margin, (canvas.height / 5).toFloat() - margin)
            canvas.drawRoundRect(rect, 20f, 20f, paint)
            val textWidth = paint2.measureText(i.toString())
            canvas.drawText(key, rect.centerX() - (textWidth / 2), rect.centerY(), paint2)
        }	
    }

    override fun onLayout(
        changed: Boolean,
        width: Int,
        height: Int,
        oldWidth: Int,
        oldHeight: Int
    ) {
    }
}
