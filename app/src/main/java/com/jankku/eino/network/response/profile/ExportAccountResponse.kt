package com.jankku.eino.network.response.profile

import com.jankku.eino.data.model.Book
import com.jankku.eino.data.model.Movie

data class ExportAccountResponse(
    val profile: ProfileResponse,
    val books: List<Book>,
    val movies: List<Movie>,
    val shares: List<Share>
)