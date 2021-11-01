package com.jankku.eino.util

import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HideFabOnScroll(private val fab: FloatingActionButton) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 5) {
            fab.hide()
        } else if (dy < 0) {
            fab.show()
        }
    }
}