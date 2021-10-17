package com.jankku.eino.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val movie_id: String,
    val title: String,
    val studio: String,
    val director: String,
    val writer: String,
    val duration: Int,
    val year: Int,
    val status: String,
    val score: Int,
    val start_date: String,
    val end_date: String,
    val created_on: String
) : Parcelable