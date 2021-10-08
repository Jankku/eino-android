package com.jankku.eino.util

import android.view.WindowManager
import androidx.fragment.app.Fragment

fun Fragment.blockInput() {
    activity?.window?.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )
}

fun Fragment.unblockInput() {
    activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun Fragment.blockInputForTask(task: () -> Unit) {
    blockInput()
    task.invoke()
    unblockInput()
}