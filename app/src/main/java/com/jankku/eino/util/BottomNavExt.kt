package com.jankku.eino.util

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.jankku.eino.ui.MainActivity

fun hideBottomNav(activity: FragmentActivity) {
    if (activity is MainActivity) {
        activity.setBottomNavigationVisibility(View.GONE)
    }
}

fun showBottomNav(activity: FragmentActivity) {
    if (activity is MainActivity) {
        activity.setBottomNavigationVisibility(View.VISIBLE)
    }
}