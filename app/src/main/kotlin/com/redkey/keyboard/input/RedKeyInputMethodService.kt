package com.redkey.keyboard.input

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.view.View
import android.view.ViewGroup.LayoutParams
import com.redkey.keyboard.R
import com.redkey.keyboard.view.KeyboardView

class RedKeyInputMethodService : InputMethodService(), View.OnClickListener {
    override fun onCreateInputView(): View {
        val keyboard = KeyboardView(this)
        val view = View(this)
        view.layoutParams = LayoutParams(keyboard.width, keyboard.height / 5)
        view.setBackgroundColor(0xFF000000.toInt())

        keyboard.addView(view)
        return KeyboardView(this)
    }

    override fun onClick(view: View) {
        //TODO: Handle button presses
    }

    private fun setupListeners(view: KeyboardView) {
        //TODO: Setup listeners
    }
}
