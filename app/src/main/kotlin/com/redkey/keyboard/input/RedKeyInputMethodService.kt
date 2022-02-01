package com.redkey.keyboard.input

import android.inputmethodservice.InputMethodService
import android.view.View
import com.redkey.keyboard.databinding.KeyboardViewBinding
import com.redkey.keyboard.R

class RedKeyInputMethodService : InputMethodService(), View.OnClickListener {
    override fun onCreateInputView(): View {
        //val binding = KeyboardViewBinding.inflate(layoutInflater)
        //setupListeners(binding)
        val view = layoutInflater.inflate(R.layout.keyboard_view, null)
        android.widget.Toast.makeText(this, view.height.toString(), 5).show()

        return view
    }

    override fun onClick(view: View) {
        //TODO: Handle button presses
    }

    private fun setupListeners(binding: KeyboardViewBinding) {
        //TODO: Setup listeners
    }
}
