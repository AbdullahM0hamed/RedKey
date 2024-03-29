package com.redkey.keyboard.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.redkey.keyboard.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)

        if (!isTaskRoot) {
            finish()
            return
        }

        val binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        var enabled = false
        for (inputmethod in manager.getEnabledInputMethodList()) {
            if (inputmethod.packageName == "com.redkey.keyboard") {
                enabled = true
                break
            }
        }

        if (!enabled) {
            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            Toast.makeText(this, "Enable RedKey", 5).show()
        }
    }
}
