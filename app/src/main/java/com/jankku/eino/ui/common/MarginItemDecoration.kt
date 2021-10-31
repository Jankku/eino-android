package com.jankku.eino.ui.common

import android.content.res.Configuration
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(
    private val orientation: Int,
    private val columns: Int,
    private val space: Float
) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % columns
        val row = position / columns

        if (row == 0) outRect.top = space.toInt()

        when (orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                when (columns) {
                    1 -> applyMargin(outRect, space, space, space)
                    2 -> {
                        when (column) {
                            0 -> applyMargin(outRect, space, space / 2, space)
                            1 -> applyMargin(outRect, space / 2, space, space)
                        }
                    }
                }
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                when (columns) {
                    1 -> applyMargin(outRect, space * 2, space * 2, space)
                    2 -> {
                        when (column) {
                            0 -> applyMargin(outRect, space * 2, space / 2, space)
                            1 -> applyMargin(outRect, space / 2, space * 2, space)
                        }
                    }
                    3 -> {
                        when (column) {
                            0 -> applyMargin(outRect, space * 2, space / 2, space)
                            1 -> applyMargin(outRect, space / 2, space / 2, space)
                            2 -> applyMargin(outRect, space / 2, space * 2, space)
                        }
                    }
                }
            }
        }
    }

    private fun applyMargin(outRect: Rect, left: Float, right: Float, bottom: Float) {
        outRect.left = left.toInt()
        outRect.right = right.toInt()
        outRect.bottom = bottom.toInt()
    }
}