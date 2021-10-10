package com.jankku.eino.network.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val book_id: String,
    val isbn: String,
    val title: String,
    val author: String,
    val publisher: String,
    val pages: Int,
    val year: Int,
    val status: String,
    val score: Int,
    val start_date: String,
    val end_date: String,
    val created_on: String
) : Parcelable