package com.jankku.eino.network.response.movie

import com.jankku.eino.data.model.Movie

data class MovieSearchResponse(
    val results: List<Movie>
)
