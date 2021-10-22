package com.jankku.eino.util

import android.app.Activity
import com.jankku.eino.R
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

/**
 *  Formats UTC time to local time
 *  @param date the date string to convert
 *  @return Converted date string
 */
fun utcToLocal(date: String): String = LocalDateTime
    .parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    .atOffset(ZoneOffset.UTC)
    .atZoneSameInstant(ZoneId.systemDefault())
    .format(DateTimeFormatter.ISO_LOCAL_DATE)
    .toString()

/**
 *  Date picker util. If string is "Today" return current date, else return string supplied
 *  @param string String to check
 *  @return Either current date in "yyyy-MM-dd" format or the string supplied
 */
fun getDateFromString(string: String, activity: Activity): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = formatter.format(Date())
    val dateFieldItems = activity.resources.getStringArray(R.array.date_field_items)

    return if (string == dateFieldItems[0]) {
        today
    } else {
        string
    }
}