package com.redkey.keyboard.input

import android.inputmethodservice.InputMethodService
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams
import com.redkey.keyboard.R
import com.redkey.keyboard.view.KeyboardView

class RedKeyInputMethodService : InputMethodService(), View.OnClickListener {
    override fun onCreateInputView(): View {
        return KeyboardView(
            this,
            listOf(
                listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
                listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
                listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
                listOf("SHIFT", "z", "x", "c", "v", "b", "n", "m", "BACKSPACE")
            )
        )
    }

    override fun onClick(view: View) { }
}
