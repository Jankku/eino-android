package com.jankku.eino.ui.common

import android.content.res.Configuration
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(
    private val orientation: Int,
    private val columns: Int,
    private val space: Float
) : RecyclerView.ItemDecoration() {
    private val halfSpace = space / 2
    private val doubleSpace = space * 2

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
                            0 -> applyMargin(outRect, space, halfSpace, space)
                            1 -> applyMargin(outRect, halfSpace, space, space)
                        }
                    }
                    3 -> {
                        when (column) {
                            0 -> applyMargin(outRect, space, halfSpace, space)
                            1 -> applyMargin(outRect, space, space, space)
                            2 -> applyMargin(outRect, halfSpace, space, space)
                        }
                    }
                }
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                when (columns) {
                    1 -> applyMargin(outRect, doubleSpace, doubleSpace, space)
                    2 -> {
                        when (column) {
                            0 -> applyMargin(outRect, doubleSpace, halfSpace, space)
                            1 -> applyMargin(outRect, halfSpace, doubleSpace, space)
                        }
                    }
                    3 -> {
                        when (column) {
                            0 -> applyMargin(outRect, doubleSpace, halfSpace, space)
                            1 -> applyMargin(outRect, halfSpace, halfSpace, space)
                            2 -> applyMargin(outRect, halfSpace, doubleSpace, space)
                        }
                    }
                    4 -> {
                        when (column) {
                            0 -> applyMargin(outRect, doubleSpace, halfSpace, space)
                            1 -> applyMargin(outRect, halfSpace, halfSpace, space)
                            2 -> applyMargin(outRect, halfSpace, halfSpace, space)
                            3 -> applyMargin(outRect, halfSpace, doubleSpace, space)
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