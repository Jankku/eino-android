package com.jankku.eino.util

import android.os.CountDownTimer
import androidx.appcompat.widget.SearchView

class DebounceTextWatcher(
    private val delay: Long = 500L,
    private val block: (String?) -> Unit
) : SearchView.OnQueryTextListener {
    private var timer: CountDownTimer? = null

    override fun onQueryTextChange(newText: String?): Boolean {
        timer?.cancel()
        timer = object : CountDownTimer(delay, delay) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                block(newText)
            }
        }.start()
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean = true
}