package com.redkey.keyboard.ui.main

import android.os.Bundle
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
    }
}
