package com.jankku.eino.network.response

import com.jankku.eino.data.model.Book

data class BookListResponse(
    val results: List<Book>
)