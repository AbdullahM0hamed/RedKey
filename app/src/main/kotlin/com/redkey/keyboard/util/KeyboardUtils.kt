package com.redkey.keyboard.util

object KeyboardUtils {
    public fun getKeys(page: Int): List<List<String>> {
        return listOf(
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
            listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
            listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
            listOf("SHIFT", "z", "x", "c", "v", "b", "n", "m", "BACKSPACE"),
            listOf("NUMBERS", "EMOJIS", "COMMA", "SPACE", "PERIOD", "ENTER")
        )
    }
}
