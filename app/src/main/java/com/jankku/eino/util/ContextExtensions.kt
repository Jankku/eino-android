package com.jankku.eino.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.jankku.eino.R

fun Context.isTablet() = this.resources.getBoolean(R.bool.isTablet)

fun Context.isNotTablet() = !this.resources.getBoolean(R.bool.isTablet)

fun Context.copyToClipboard(text: CharSequence) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Share", text)
    clipboard.setPrimaryClip(clip)
}
