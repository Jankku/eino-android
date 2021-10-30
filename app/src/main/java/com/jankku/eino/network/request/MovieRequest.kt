package com.jankku.eino.network.request

data class MovieRequest(
    val title: String,
    val studio: String,
    val director: String,
    val writer: String,
    val duration: Int,
    val year: Int,
    val status: String,
    val score: Int,
    val start_date: String,
    val end_date: String
)