package com.jankku.eino.util

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.jankku.eino.R

fun applyTheme(activity: Activity, value: String, shouldRecreate: Boolean = false) {
    when (value) {
        activity.getString(R.string.settings_theme_value_light) -> AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_NO
        )
        activity.getString(R.string.settings_theme_value_dark) -> AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_YES
        )
        else -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            }
        }
    }
    if (shouldRecreate) activity.recreate()
}