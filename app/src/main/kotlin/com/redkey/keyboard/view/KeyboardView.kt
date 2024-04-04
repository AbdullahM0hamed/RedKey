package com.redkey.keyboard.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.inputmethod.CursorAnchorInfo
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.inputmethodservice.InputMethodService
import androidx.core.graphics.drawable.DrawableCompat
import com.redkey.keyboard.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class KeyboardView(
    val ctx: InputMethodService,
    val keys: List<List<String>>
) : ViewGroup(ctx) {

    private var shiftState = ShiftState.OFF
    enum class ShiftState {
        OFF, ON, LOCKED
    }

    var job: Job? = null
    var keyHeld = false
    var backspace = false
    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) {
            vibrate()
            val largest = keys.sortedBy { it.size }.get(keys.size - 1)
            val rowVal = Math.ceil(e.y.toDouble() / (height / 5).toDouble()).toInt() - 1

            if (rowVal < keys.size) {
                if (keys[rowVal].contains("SPACE")) {
                    if (spaceRow.isNotEmpty()) {
                        for (pair in spaceRow) {
                            val rect = pair.second
                            if (e.x >= rect.left && e.x <= rect.right) {
                                keyAction(pair.first)
                                break
                            }
                        }
                    }

                    return false
                }

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
        } else if (e.action == MotionEvent.ACTION_UP) {
            keyHeld = false
            job?.cancel()
            if (backspace) {
                ctx.currentInputConnection.erase(1, 0)
            }
        }

        return true
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
                connection.deleteText(1, 0)
                backspace = true
            }
            "SPACE" -> connection.writeText(" ", 1)
            "EMOJIS" -> {}
            "NUMBERS" -> {}
            "COMMA" -> connection.writeText(",", 1)
            "PERIOD" -> connection.writeText(".", 1)
            "ENTER" -> connection.writeText("\n", 1)
            else -> {
                when (shiftState) {
                    ShiftState.OFF -> connection.writeText(key.toLowerCase(), 1)
                    ShiftState.ON -> {
                        connection.writeText(key.toUpperCase(), 1)
                        shiftState = ShiftState.OFF
                        invalidate()
                    }
                    ShiftState.LOCKED -> connection.writeText(key.toUpperCase(), 1)
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
        spaceShift = 0f
        spaceRow.clear()
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
                    isExtraWidth(key),
                    row.contains("SPACE"),
                    key == "SPACE"
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
        return listOf("SHIFT", "BACKSPACE", "ENTER").contains(key)
    }

    private fun Canvas.drawIcon(
        icon: Drawable,
        rect: RectF
    ) {
        val width = rect.right - rect.left
        val height = rect.bottom - rect.top
        val heightWidthRatio = icon.intrinsicHeight / icon.intrinsicWidth
        val newWidth = (rect.right - rect.left) - (2 * (width * 0.3))
        val newHeight = newWidth * heightWidthRatio
        val top = (height - newHeight) / 2
        val bottom = rect.bottom - top
        val bounds = Rect(
            (rect.left + (width * 0.3)).toInt(),
            //(rect.top + (height * 0.3)).toInt(),
            (rect.top + top).toInt(),
            (rect.right - (width * 0.3)).toInt(),
            //(rect.bottom - (height * 0.3)).toInt()
            bottom.toInt()
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
            "NUMBERS" -> canvas.drawText("123", rect, paint)
            "EMOJIS" -> {
                val icon = ctx.resources.getDrawable(R.drawable.ic_emojis, null)
                canvas.drawIcon(icon, rect)
            }
            "COMMA" -> canvas.drawText(",", rect, paint)
            "SPACE" -> {}
            "PERIOD" -> canvas.drawText(".", rect, paint)
            "ENTER" -> {
                val icon = ctx.resources.getDrawable(R.drawable.ic_enter, null)
                canvas.drawIcon(icon, rect)
            }
            else -> {
                val text = if (shiftState == ShiftState.OFF) {
                    key.toLowerCase()
                } else {
                    key.toUpperCase()
                }
                canvas.drawText(text, rect, paint)
            }
        }
    }

    private fun Canvas.drawText(
        text: String,
        rect: RectF,
        paint: Paint
    ) {
        val textWidth = paint.measureText(text)
        drawText(
            text,
            rect.centerX() - (textWidth / 2),
            rect.centerY(),
            paint
        )
    }

    private var spaceShift = 0f
    private var spaceRow = mutableListOf<Pair<String, RectF>>()
    private fun getKeyRect(
        largestSize: Int,
        rowSize: Int,
        width: Int,
        height: Int,
        row: Int,
        col: Int,
        margin: Float,
        isExtraWidth: Boolean,
        isSpaceRow: Boolean,
        isSpace: Boolean
    ): RectF {
        val rowShift = if (rowSize < largestSize && !isSpaceRow) {
            (width / largestSize / 2).toFloat()
        } else {
            0f
        }

        val isStartKey = col == 0
        val left = if (isExtraWidth && isStartKey) {
            0f
        } else if (isExtraWidth && !isStartKey && !isSpaceRow) {
            rowShift + ((width / largestSize) * col).toFloat() + margin
        } else if (isExtraWidth && isSpaceRow) {
            rowShift + ((width / largestSize) * col).toFloat()
        } else {
            rowShift + ((width / largestSize) * col).toFloat()
        }

        val right = if (isExtraWidth && isStartKey) {
            ((width / largestSize) * (col + 1)).toFloat() - margin + rowShift - margin
        } else if (isExtraWidth && !isStartKey) {
            width.toFloat() - spaceShift
        } else if (isSpace) {
            width - (2.5 * (width / largestSize)).toFloat()
        } else {
            ((width / largestSize) * (col + 1)).toFloat() - margin + rowShift
        }

        val rect =  RectF(
            left + spaceShift,
            ((height / 5) * row).toFloat() + margin,
            right + spaceShift,
            ((height / 5) * (row + 1)).toFloat()
        )

        if (isSpaceRow) {
            spaceRow.add(
                Pair(
                    keys[row][col],
                    rect
                )
            )
        }

        if (isSpace) {
            spaceShift = right - (((width / largestSize) * (col + 1)).toFloat() - margin + rowShift)
        }

        return rect
    }

    override fun onLayout(
        changed: Boolean,
        width: Int,
        height: Int,
        oldWidth: Int,
        oldHeight: Int
    ) {
    }

    var composed = ""
    var pos = 0
    private fun InputConnection.writeText(text: String, position: Int) {
        pos += position
        if (text == " " || text == "\n") {
            commitText(composed + text, position)
            composed = ""
        } else {
            composed += text
            setComposingText(composed, position)
        }
    }

    private fun InputConnection.deleteText(before: Int, after: Int) {
        keyHeld = true
        job = GlobalScope.launch(Dispatchers.Main.immediate) {
            while (keyHeld) {
                delay(500L)
                val extractedText = getExtractedText(ExtractedTextRequest(), 0)
                var charPos = extractedText.selectionStart
                if (charPos == 0) {
                    break
                }
                var beforeCursor = extractedText.text[charPos - 1]
                while (beforeCursor == ' ') {
                    erase(before, after)
                    charPos -= 1
                    if (charPos == 0) {
                        break
                    }
                    beforeCursor = extractedText.text[charPos - 1]
                }

                beforeCursor = extractedText.text[charPos - 1]
                while (beforeCursor != ' ') {
                    erase(before, after)
                    charPos -= 1
                    if (charPos == 0) {
                        break
                    }
                    beforeCursor = extractedText.text[charPos - 1]
                }
            }
        }
    }

    private fun InputConnection.erase(before: Int, after: Int) {
        if (pos - before + after >= 0) {
            pos = pos - before + after
        }

        backspace = false
        if (selectionEnd != 0) {
            commitText("", 1)
        }

        if (composed.isEmpty()) {
            deleteSurroundingText(before, after)
        } else {
            composed = composed.dropLast(before)
            setComposingText(composed, pos)
        }
        vibrate()
    }

    var selectionEnd = 0
    public fun onUpdateCursorAnchorInfo(cursorAnchorInfo: CursorAnchorInfo) {
        if (cursorAnchorInfo.selectionEnd != cursorAnchorInfo.selectionStart) {
            selectionEnd = cursorAnchorInfo.selectionEnd
        } else {
            selectionEnd = 0
        }

        if (cursorAnchorInfo.selectionStart == pos) {
            return
        }

        val connection = ctx.currentInputConnection
        connection.finishComposingText()
        composed = ""
    }
}
