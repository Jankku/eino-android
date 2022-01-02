package com.jankku.eino.network.response.book

import com.jankku.eino.data.model.BookSearchItem

data class BookSearchResponse(
    val results: List<BookSearchItem>
)
