package com.jankku.eino.network.request

data class AddBookRequest(
    val isbn: String,
    val title: String,
    val author: String,
    val publisher: String,
    val pages: Int = 0,
    val year: Int = 2021,
    val status: String,
    val score: Int,
    val start_date: String,
    val end_date: String,
)
