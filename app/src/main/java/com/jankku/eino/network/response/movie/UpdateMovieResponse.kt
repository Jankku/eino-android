package com.jankku.eino.network.response.movie

import com.jankku.eino.data.model.Movie

data class UpdateMovieResponse(
    val results: List<Movie>
)