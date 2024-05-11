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
import android.text.InputType
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
import com.redkey.keyboard.emoji.EmojiHandler
import com.redkey.keyboard.util.KeyboardUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class KeyboardView(val ctx: InputMethodService) : ViewGroup(ctx), EmojiHandler.EmojiListener {
    public var editor: EditorInfo? = null
    public var page = 0
    public var keys = KeyboardUtils.getKeys(page)
    public var shiftState = ShiftState.OFF
    public enum class ShiftState {
        OFF, ON, LOCKED
    }

    private val keyBg = Paint()
    private val textPaint = Paint()
    init {
        EmojiHandler.listener = this
        keyBg.color = 0x66888888.toInt()
        textPaint.color = 0xFFFF0000.toInt()
        textPaint.textSize = 24f
    }

    var currentRects: MutableList<MutableList<Pair<String, RectF>>> = mutableListOf()
    var rowCoords: MutableList<Pair<Float, Float>> = mutableListOf()
    var job: Job? = null
    var keyHeld = false
    public var backspace = false
    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (page == 3) {
            return EmojiHandler.onTouchEvent(this, e)
        }

        if (e.action == MotionEvent.ACTION_DOWN) {
            for ((i, coord) in rowCoords.withIndex()) {
                if (e.y >= coord.first && e.y <= coord.second) {
                    val col = getColumn(e.x, i)
                    vibrate()
                    KeyboardUtils.keyAction(ctx, this, currentRects[i][col].first, currentRects[i][col].second)
                    break
                }
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

    override fun onEmojiClicked(emoji: String) {
        ctx.currentInputConnection.commitText(emoji, 1)
    }

    override fun onButtonClicked(deleteKey: Boolean) {
        if (deleteKey) {
            deleteText(ctx.currentInputConnection, 1, 0)
            backspace = true
        } else {
            page = 0
            keys = KeyboardUtils.getKeys(0)
            invalidate()
        }
    }

    private fun getColumn(x: Float, i: Int): Int {
        var low = 0
        var high = currentRects[i].size - 1

        var mid: Int = 0
        while (low <= high) {
            mid = low + (high - low) / 2
            val rect = currentRects[i][mid].second
            when {
                x >= rect.left && x <= rect.right -> return mid
                x < rect.left -> high = mid - 1
                x > rect.right -> low = mid + 1
            }
        }

        return mid
    }

    private fun vibrate() {
        val vibrator = ctx.getSystemService(Vibrator::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator?.vibrate(50)
        }
    }

    override fun computeScroll() {
        val scroller = EmojiHandler.scroller
        if (scroller != null && scroller.computeScrollOffset()) {
            if (scroller.getCurrX() > 0) {
                EmojiHandler.scrollX = scroller.getCurrX().toFloat()
                postInvalidate()
                return
            }

            EmojiHandler.scrollY = scroller.getCurrY().toFloat()
            scrollTo(0, scroller.getCurrY())
            postInvalidate()
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

    private val bg = 0xFF000000.toInt()
    private val margin = 10f
    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawColor(bg)
        if (page == 3 ) {
            return EmojiHandler.onDraw(this, canvas)
        }

        spaceShift = 0f
        spaceRow.clear()

        val largest = keys.sortedBy { it.size }.get(keys.size - 1)
        currentRects.clear()
        rowCoords.clear()
        keys.forEachIndexed { rowIndex, row ->
            val gridRow: MutableList<Pair<String, RectF>> = mutableListOf()
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
                    key == "SPACE",
                    keys.size
                )
                gridRow.add(Pair(key, rect))
                canvas.drawRoundRect(rect, 20f, 20f, keyBg)
                KeyboardUtils.drawKey(
                    ctx,
                    this,
                    canvas,
                    textPaint,
                    key,
                    rect
                )
            }
            currentRects.add(gridRow)
            val first = gridRow[0].second
            rowCoords.add(Pair(first.top, first.bottom))
        }
    }

    private fun isExtraWidth(key: String): Boolean {
        return listOf("SHIFT", "BACKSPACE", "ENTER").contains(key)
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
        isSpace: Boolean,
        rowCount: Int
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
            ((height / rowCount) * row).toFloat() + margin,
            right + spaceShift,
            ((height / rowCount) * (row + 1)).toFloat()
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
    public fun writeText(conn: InputConnection, text: String, position: Int) {
        if (editor != null && editor!!.inputType == InputType.TYPE_NULL) {
            conn.commitText(text, position)
            composed = ""
            return
        }

        pos += position
        if (text == " " || text == "\n") {
            conn.commitText(composed + text, position)
            composed = ""
        } else {
            composed += text
            conn.setComposingText(composed, position)
        }
    }

    public fun deleteText(conn: InputConnection, before: Int, after: Int) {
        keyHeld = true
        job = GlobalScope.launch(Dispatchers.Main.immediate) {
            while (keyHeld) {
                delay(500L)
                val extractedText = conn.getExtractedText(ExtractedTextRequest(), 0)
                var charPos = 0
                try {
                    charPos = extractedText.selectionStart
                } catch (e: Exception) {}
                if (charPos == 0) {
                    break
                }
                var beforeCursor = extractedText.text[charPos - 1]
                while (beforeCursor == ' ') {
                    conn.erase(before, after)
                    charPos -= 1
                    if (charPos == 0) {
                        break
                    }
                    beforeCursor = extractedText.text[charPos - 1]
                }

                beforeCursor = extractedText.text[charPos - 1]
                while (beforeCursor != ' ') {
                    conn.erase(before, after)
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
