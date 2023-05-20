package com.jankku.eino.network.response.book

import com.jankku.eino.data.model.Book

data class BookSearchResponse(
    val results: List<Book>
)
