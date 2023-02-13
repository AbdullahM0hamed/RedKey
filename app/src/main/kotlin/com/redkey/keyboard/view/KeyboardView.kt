package com.redkey.keyboard.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.inputmethod.ExtractedTextRequest
import android.inputmethodservice.InputMethodService
import androidx.core.graphics.drawable.DrawableCompat
import com.redkey.keyboard.R

class KeyboardView(
    val ctx: InputMethodService,
    val keys: List<List<String>>
) : ViewGroup(ctx) {

    private var shiftState = ShiftState.OFF
    enum class ShiftState {
        OFF, ON, LOCKED
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) {
	    vibrate()
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

                keyAction(keys[rowVal][col])
            }
        }

        return false
    }

    private fun vibrate() {
            val vibrator = ctx.getSystemService(Vibrator::class.java)

	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
	    } else {
	        vibrator.vibrate(50)
	    }
    }

    private fun keyAction(key: String) {
        val connection = ctx.currentInputConnection
        when (key) {
            "SHIFT" -> {
	        shiftState = ShiftState.values()[(shiftState.ordinal + 1) % ShiftState.values().size]
		invalidate()
	    }
            "BACKSPACE" -> {
                connection.deleteSurroundingText(1, 0)
            }
            else -> {
	    	when (shiftState) {
		    ShiftState.OFF -> connection.commitText(key.toLowerCase(), 1)
		    ShiftState.ON -> {
		        connection.commitText(key.toUpperCase(), 1)
			shiftState = ShiftState.OFF
			invalidate()
		    }
		    ShiftState.LOCKED -> connection.commitText(key.toUpperCase(), 1)
		}
	    }
        }
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
                    margin,
                    isExtraWidth(key)
                )
                canvas.drawRoundRect(rect, 20f, 20f, paint)
                drawKey(
                    canvas,
                    paint2,
                    key,
                    rect
                )
            }
        }
    }

    private fun isExtraWidth(key: String): Boolean {
        return listOf("SHIFT", "BACKSPACE").contains(key)
    }

    private fun Canvas.drawIcon(
        icon: Drawable,
        rect: RectF
    ) {
        val width = rect.right - rect.left
        val height = rect.bottom - rect.top
        val bounds = Rect(
            (rect.left + (width * 0.3)).toInt(),
            (rect.top + (height * 0.3)).toInt(),
            (rect.right - (width * 0.3)).toInt(),
            (rect.bottom - (height * 0.3)).toInt()
        )
	val wrappedIcon = DrawableCompat.wrap(icon)
	DrawableCompat.setTint(wrappedIcon, 0xFFFF0000.toInt())
        wrappedIcon.bounds = bounds
        wrappedIcon.draw(this)
    }

    private fun drawKey(
        canvas: Canvas,
        paint: Paint,
        key: String,
        rect: RectF
    ) {
        when (key) {
            "SHIFT" -> {
                val icon = ctx.resources.getDrawable(
		    when (shiftState) {
		        ShiftState.OFF -> R.drawable.ic_shift
			ShiftState.ON -> R.drawable.ic_shift_pressed
			ShiftState.LOCKED -> R.drawable.ic_shift_locked
		    },
		    null
		)
                canvas.drawIcon(icon, rect)
            }
            "BACKSPACE" -> {
                val icon = ctx.resources.getDrawable(R.drawable.ic_backspace, null)
                canvas.drawIcon(icon, rect)
            }
            else -> {
		val text = if (shiftState == ShiftState.OFF) {
		    key.toLowerCase()
		} else {
		    key.toUpperCase()
		}
                val textWidth = paint.measureText(text)
                canvas.drawText(
                    text,
                    rect.centerX() - (textWidth / 2),
                    rect.centerY(),
                    paint
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
        margin: Float,
        isExtraWidth: Boolean
    ): RectF {
        val rowShift = if (rowSize < largestSize) {
            (width / largestSize / 2).toFloat()
        } else {
            0f
        }

        val isStartKey = col == 0
        val left = if (isExtraWidth && isStartKey) {
            0f
        } else if (isExtraWidth && !isStartKey) {
            rowShift + ((width / largestSize) * col).toFloat() + margin
        } else {
            rowShift + ((width / largestSize) * col).toFloat()
        }

        val right = if (isExtraWidth && isStartKey) {
            ((width / largestSize) * (col + 1)).toFloat() - margin + rowShift - margin
        } else if (isExtraWidth && !isStartKey) {
            width.toFloat()
        } else {
            ((width / largestSize) * (col + 1)).toFloat() - margin + rowShift
        }

        return RectF(
            left,
            ((height / 5) * row).toFloat(),
            right,
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
