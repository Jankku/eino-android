package com.jankku.eino.util

import com.github.mikephil.charting.formatter.ValueFormatter

class TableValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String = "%.0f".format(value)
}