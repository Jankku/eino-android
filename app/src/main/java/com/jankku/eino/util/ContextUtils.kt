package com.jankku.eino.util

import android.content.Context
import com.jankku.eino.R

fun Context.isTablet() = this.resources.getBoolean(R.bool.isTablet)
fun Context.isNotTablet() = !this.resources.getBoolean(R.bool.isTablet)