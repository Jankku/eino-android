package com.jankku.eino.util

import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jankku.eino.R
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

fun showNavRail(activity: FragmentActivity) {
    if (activity is MainActivity) {
        activity.setNavigationRailVisibility(View.VISIBLE)
    }
}

fun hideNavRail(activity: FragmentActivity) {
    if (activity is MainActivity) {
        activity.setNavigationRailVisibility(View.GONE)
    }
}

fun removeNavRailHeader(activity: FragmentActivity) {
    if (activity is MainActivity) {
        activity.navRail?.removeHeaderView()
    }
}

fun MainActivity.setupNavRail(headerLayout: Int, fabBlock: (FloatingActionButton) -> Unit) {
    this.navRail?.addHeaderView(headerLayout)
    val fab = this.navRail?.headerView?.findViewById<FloatingActionButton>(R.id.fabNavRailAction)
    if (fab != null) fabBlock(fab)
}