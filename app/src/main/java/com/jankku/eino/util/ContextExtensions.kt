package com.jankku.eino.util

import android.content.Context
import android.widget.Toast
import com.jankku.eino.R

fun Context.isTablet() = this.resources.getBoolean(R.bool.isTablet)

fun Context.isNotTablet() = !this.resources.getBoolean(R.bool.isTablet)

fun Context.showShortToast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Context.showLongToast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
