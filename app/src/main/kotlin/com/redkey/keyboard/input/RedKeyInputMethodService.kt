package com.redkey.keyboard.input

import android.inputmethodservice.InputMethodService
import android.view.View
import com.redkey.keyboard.databinding.KeyboardViewBinding

class RedKeyInputMethodService : InputMethodService(), View.OnClickListener {
    override fun onCreateInputView(): View {
        val binding = KeyboardViewBinding.inflate(layoutInflater)
        setupListeners(binding)
        android.widget.Toast.makeText(this, binding.root.width.toString(), 5).show()

        return binding.root
    }

    override fun onClick(view: View) {
        //TODO: Handle button presses
    }

    private fun setupListeners(binding: KeyboardViewBinding) {
        //TODO: Setup listeners
    }
}
