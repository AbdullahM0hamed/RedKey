package com.redkey.keyboard.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.redkey.keyboard.ui.main.MainActivity

class KeyboardChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val default = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.DEFAULT_INPUT_METHOD)
        if (default != "com.redkey.keyboard/.input.RedKeyInputMethodService") {
            return
        }

        val defaultIntent = Intent(context, MainActivity::class.java)
        defaultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        defaultIntent.putExtra("default", default)
        context.startActivity(defaultIntent)
    }
}
