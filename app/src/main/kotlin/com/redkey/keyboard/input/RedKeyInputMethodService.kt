package com.redkey.keyboard.input

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.inputmethod.CursorAnchorInfo
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import com.redkey.keyboard.R
import com.redkey.keyboard.view.KeyboardView

class RedKeyInputMethodService : InputMethodService(), View.OnClickListener {
    var keyboard: KeyboardView? = null
    override fun onCreateInputView(): View {
        keyboard = KeyboardView(this)
        return keyboard!!
    }

    override fun onStartInputView(editorInfo: EditorInfo, restart: Boolean) {
        currentInputConnection?.requestCursorUpdates(InputConnection.CURSOR_UPDATE_MONITOR)
    }

    override fun onUpdateCursorAnchorInfo(cursorAnchorInfo: CursorAnchorInfo) {
        keyboard?.onUpdateCursorAnchorInfo(cursorAnchorInfo)
    }

    override fun onClick(view: View) {
    }
}
