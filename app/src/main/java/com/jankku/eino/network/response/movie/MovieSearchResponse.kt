package com.jankku.eino.network.response.movie

import com.jankku.eino.data.model.MovieSearchItem

data class MovieSearchResponse(
    val results: List<MovieSearchItem>
)
