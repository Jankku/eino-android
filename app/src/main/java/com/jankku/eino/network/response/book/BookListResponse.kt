package com.jankku.eino.network.response.book

import com.jankku.eino.data.model.Book

data class BookListResponse(
    val results: List<Book>
)