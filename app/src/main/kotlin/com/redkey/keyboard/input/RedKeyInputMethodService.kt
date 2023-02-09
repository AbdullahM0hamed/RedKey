package com.redkey.keyboard.input

import android.inputmethodservice.InputMethodService
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams
import com.redkey.keyboard.R
import com.redkey.keyboard.view.KeyboardView

class RedKeyInputMethodService : InputMethodService(), View.OnClickListener {
    override fun onCreateInputView(): View {
        val keyboard = KeyboardView(this, listOf(listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"), listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")))
        return keyboard
    }

    override fun onClick(view: View) {
    }

    private fun setupListeners(view: KeyboardView) {
    }
}
