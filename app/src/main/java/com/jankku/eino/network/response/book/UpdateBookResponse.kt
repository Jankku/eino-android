package com.jankku.eino.network.response.book

import com.jankku.eino.data.model.Book

data class UpdateBookResponse(
    val results: List<Book>
)