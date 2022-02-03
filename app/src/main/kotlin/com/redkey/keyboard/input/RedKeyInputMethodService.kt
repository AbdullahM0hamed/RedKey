package com.redkey.keyboard.input

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.ViewGroup.LayoutParams
import com.redkey.keyboard.R
import com.redkey.keyboard.databinding.KeyboardViewBinding

class RedKeyInputMethodService : InputMethodService(), View.OnClickListener {
    override fun onCreateInputView(): View {
        val binding = KeyboardViewBinding.inflate(layoutInflater)
        setViewHeightDp(binding.root, 100)
        android.widget.Toast.makeText(this, binding.root.height.toString(), 5).show()

        return binding.constraint
    }

    override fun onClick(view: View) {
        //TODO: Handle button presses
    }

    private fun setupListeners(binding: KeyboardViewBinding) {
        //TODO: Setup listeners
    }
    
    private fun setViewHeightDp(view: View, height: Float) {
        val d = service.resources.displayMetrics.density
        val px = height * d + 0.5f

        view.setLayoutParams(LayoutParams(-1, px.toInt()))
    }
}
