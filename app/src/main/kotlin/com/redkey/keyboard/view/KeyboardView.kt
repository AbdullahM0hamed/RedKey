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

class KeyboardView(
    val ctx: InputMethodService,
    val keys: List<List<String>>
) : ViewGroup(ctx) {

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) {
            val largest = keys.sortedBy { it.size }.get(keys.size - 1)
            val rowVal = Math.ceil(e.y.toDouble() / (height / 5).toDouble()).toInt() - 1

            if (rowVal < keys.size) {
                val shift = if (keys[rowVal].size < largest.size) {
                    width / largest.size / 2
                } else {
                    0
                }
                val x = (e.x - shift).toDouble()
                var col = Math.ceil(x / (width / largest.size).toDouble()).toInt() - 1

                if (col < 0) {
                    col = 0
                } else if (col >= keys[rowVal].size) {
                    col = keys[rowVal].size - 1
                }

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
        val largest = keys.sortedBy { it.size }.get(keys.size - 1)
        keys.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, key ->
                val rect = getKeyRect(
                    largest.size,
                    row.size,
                    canvas.width,
                    canvas.height,
                    rowIndex,
                    colIndex,
                    margin
                )
                canvas.drawRoundRect(rect, 20f, 20f, paint)
                val textWidth = paint2.measureText(key)
                canvas.drawText(
                    key,
                    rect.centerX() - (textWidth / 2),
                    rect.centerY(),
                    paint2
                )
            }
        }
    }

    private fun getKeyRect(
        largestSize: Int,
        rowSize: Int,
        width: Int,
        height: Int,
        row: Int,
        col: Int,
        margin: Float
    ): RectF {
        val rowShift = if (rowSize < largestSize) {
            (width / largestSize / 2).toFloat()
        } else {
            0f
        }

        return RectF(
            rowShift + ((width / largestSize) * col).toFloat(),
            ((height / 5) * row).toFloat() + margin,
            ((width / largestSize) * (col + 1)).toFloat() - margin + rowShift,
            ((height / 5) * (row + 1)).toFloat() - margin
        )

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
