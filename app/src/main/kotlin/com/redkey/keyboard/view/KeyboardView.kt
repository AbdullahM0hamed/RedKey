package com.redkey.keyboard.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.inputmethodservice.InputMethodService
import android.widget.Button
import android.widget.GridLayout

class KeyboardView(
    val ctx: InputMethodService,
    val keys: List<List<String>>
) : ViewGroup(ctx) {

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        if (e?.action == MotionEvent.ACTION_DOWN) {
            val largest = keys.sortedBy { it.size }.get(keys.size - 1)
            val rowVal = Math.ceil(e.y.toDouble() / (height / 5).toDouble()).toInt() - 1
            val col = Math.ceil(e.x.toDouble() / (width / largest.size).toDouble()).toInt() - 1

            if (rowVal < keys.size && col < keys[rowVal].size) {
                ctx.currentInputConnection.commitText(keys[rowVal][col], 1)
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
        keys.forEachIndexed { j, row ->
            row.forEachIndexed { i, key ->
                val rect = RectF(((canvas.width / row.size) * i).toFloat(), ((canvas.height / 5) * j).toFloat() + margin, ((canvas.width / row.size) * (i + 1)).toFloat() - margin, ((canvas.height / 5) * (j + 1)).toFloat() - margin)
                canvas.drawRoundRect(rect, 20f, 20f, paint)
                val textWidth = paint2.measureText(i.toString())
                canvas.drawText(key, rect.centerX() - (textWidth / 2), rect.centerY(), paint2)
            }
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
