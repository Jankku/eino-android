package com.jankku.eino.util

import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher

class DebounceTextWatcher(
    private val delay: Long = 500L,
    private val block: (Editable?) -> Unit
) : TextWatcher {
    private var timer: CountDownTimer? = null

    override fun afterTextChanged(s: Editable?) {
        timer?.cancel()
        timer = object : CountDownTimer(delay, delay) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                block(s)
            }
        }.start()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}