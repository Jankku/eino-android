package com.jankku.eino.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun utcToLocal(date: String): String = LocalDateTime
    .parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    .atOffset(ZoneOffset.UTC)
    .atZoneSameInstant(ZoneId.systemDefault())
    .format(DateTimeFormatter.ISO_LOCAL_DATE)
    .toString()