package com.redkey.keyboard.input

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.view.View
import android.view.ViewGroup.LayoutParams
import com.redkey.keyboard.R
import com.redkey.keyboard.view.KeyboardView

class RedKeyInputMethodService : InputMethodService(), View.OnClickListener {
    override fun onCreateInputView(): View {
        return KeyboardView(this)
    }

    override fun onClick(view: View) {
        //TODO: Handle button presses
    }

    private fun setupListeners(view: KeyboardView) {
        //TODO: Setup listeners
    }
}
