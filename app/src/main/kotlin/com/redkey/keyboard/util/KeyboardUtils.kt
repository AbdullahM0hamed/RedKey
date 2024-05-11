package com.redkey.keyboard.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.inputmethodservice.InputMethodService
import android.os.Handler
import android.widget.PopupWindow
import android.widget.TextView
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.redkey.keyboard.R
import com.redkey.keyboard.view.KeyboardView
import com.redkey.keyboard.view.KeyboardView.ShiftState

object KeyboardUtils {
    val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    val default: List<List<List<String>>> = listOf(
        listOf(
            numbers,
            listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
            listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
            listOf("SHIFT", "z", "x", "c", "v", "b", "n", "m", "BACKSPACE"),
            listOf("NUMBERS", "EMOJIS", "COMMA", "SPACE", "PERIOD", "ENTER")
        ),
        listOf(
            numbers,
            listOf("@", "#", "£", "&", "_", "-", "(", ")", "=", "%"),
            listOf("[", "]", "{", "}", "<", ">", "^", "`", "~", "|"),
            listOf("PAGE3", "\"", "*", "'", ":", "/", "!", "?", "+", "BACKSPACE"),
            listOf("PAGE1", ",", "SPACE", ".", "ENTER")
        ),
        listOf(
            numbers,
            listOf("$", "€", "¥", "¢", "©", "®", "™", "¿", "¡", ";"),
            listOf("÷", "\\", "¦", "¬", "×", "§", "¶", "°", "…", "•"),
            listOf("PAGE2", "○", "●", "□", "■", "♤", "♡", "◇", "♧", "BACKSPACE"),
            listOf("PAGE1", ",", "SPACE", ".", "ENTER")
        )
    )

    public fun getKeys(page: Int): List<List<String>> = default[page]

    public fun keyAction(ctx: InputMethodService, keyboard: KeyboardView, key: String, rect: RectF) {
        var popup: PopupWindow
        if (key.length == 1) {
            popup = PopupWindow(
                (rect.right - rect.left).toInt(),
                ((rect.bottom - rect.top) * 2).toInt()
            )
            popup.setClippingEnabled(false)
            val view = TextView(ctx)
            view.gravity = Gravity.CENTER
            view.text = key
            view.setTextColor(0xFF000000.toInt())
            view.setBackgroundResource(R.drawable.popup)
            popup.contentView = view
            popup.showAsDropDown(keyboard, rect.left.toInt(), rect.bottom.toInt(), Gravity.NO_GRAVITY)
        } else {
            popup = PopupWindow(
                (rect.right - rect.left).toInt(),
                (rect.bottom - rect.top).toInt()
            )
            popup.setClippingEnabled(false)
            val view = View(ctx)
            view.setBackgroundResource(R.drawable.transparent_popup)
            popup.contentView = view
            popup.showAsDropDown(keyboard, rect.left.toInt(), rect.bottom.toInt(), Gravity.NO_GRAVITY)
        }
        val handler = Handler()
        handler.postDelayed({
            popup.dismiss()
        }, 250)
        val connection = ctx.currentInputConnection
        when (key) {
            "SHIFT" -> {
                keyboard.shiftState = ShiftState.values()[(keyboard.shiftState.ordinal + 1) % ShiftState.values().size]
                keyboard.invalidate()
            }
            "BACKSPACE" -> {
                keyboard.deleteText(connection, 1, 0)
                keyboard.backspace = true
            }
            "SPACE" -> keyboard.writeText(connection, " ", 1)
            "EMOJIS" -> {
	        keyboard.page = 3
		keyboard.invalidate()
	    }
            "NUMBERS" -> {
                keyboard.page += 1
                keyboard.keys = getKeys(keyboard.page)
                keyboard.invalidate()
            }
            "PAGE1" -> {
                keyboard.page = 0
                keyboard.keys = getKeys(0)
                keyboard.invalidate()
            }
            "PAGE2" -> {
                keyboard.page = 1
                keyboard.keys = getKeys(1)
                keyboard.invalidate()
            }
            "PAGE3" -> {
                keyboard.page = 2
                keyboard.keys = getKeys(2)
                keyboard.invalidate()
            }
            "COMMA" -> keyboard.writeText(connection, ",", 1)
            "PERIOD" -> keyboard.writeText(connection, ".", 1)
            "ENTER" -> keyboard.writeText(connection, "\n", 1)
            else -> {
                when (keyboard.shiftState) {
                    ShiftState.OFF -> keyboard.writeText(connection, key.toLowerCase(), 1)
                    ShiftState.ON -> {
                        keyboard.writeText(connection, key.toUpperCase(), 1)
                        keyboard.shiftState = ShiftState.OFF
                        keyboard.invalidate()
                    }
                    ShiftState.LOCKED -> keyboard.writeText(connection, key.toUpperCase(), 1)
                }
            }
        }
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
            (rect.top + top).toInt(),
            (rect.right - (width * 0.3)).toInt(),
            bottom.toInt()
        )
        val wrappedIcon = DrawableCompat.wrap(icon)
        DrawableCompat.setTint(wrappedIcon, 0xFFFF0000.toInt())
        wrappedIcon.bounds = bounds
        wrappedIcon.draw(this)
    }

    public fun drawKey(
        ctx: Context,
        keyboard: KeyboardView,
        canvas: Canvas,
        paint: Paint,
        key: String,
        rect: RectF
    ) {
        when (key) {
            "SHIFT" -> {
                val icon = ctx.resources.getDrawable(
                    when (keyboard.shiftState) {
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
            "PAGE1" -> canvas.drawText("abc", rect, paint)
            "PAGE2" -> canvas.drawText("2/2", rect, paint)
            "PAGE3" -> canvas.drawText("1/2", rect, paint)
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
                val text = if (keyboard.shiftState == ShiftState.OFF) {
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
}
