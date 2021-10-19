package com.jankku.eino.network.response

import com.jankku.eino.data.model.Movie

data class MovieListResponse(
    val results: List<Movie>
)