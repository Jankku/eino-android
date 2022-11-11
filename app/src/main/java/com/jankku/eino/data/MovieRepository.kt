package com.jankku.eino.data

import com.jankku.eino.network.EinoApi
import com.jankku.eino.network.request.MovieRequest
import com.jankku.eino.util.handleResponse
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val api: EinoApi,
    private val moshi: Moshi
) {
    suspend fun searchMovies(query: String) =
        handleResponse(api.searchMovies(query), moshi).flowOn(Dispatchers.IO)

    suspend fun getMoviesByStatus(status: String) =
        handleResponse(api.getMoviesByStatus(status), moshi).flowOn(Dispatchers.IO)

    suspend fun getMovieById(id: String) =
        handleResponse(api.getMovieById(id), moshi).flowOn(Dispatchers.IO)

    suspend fun addMovie(movie: MovieRequest) =
        handleResponse(api.addMovie(movie), moshi).flowOn(Dispatchers.IO)

    suspend fun editMovie(id: String, movie: MovieRequest) =
        handleResponse(api.updateMovie(id, movie), moshi).flowOn(Dispatchers.IO)

    suspend fun deleteMovie(id: String) =
        handleResponse(api.deleteMovie(id), moshi).flowOn(Dispatchers.IO)
}