package com.jankku.eino.data

import com.jankku.eino.network.EinoApi
import com.jankku.eino.network.request.MovieRequest
import com.jankku.eino.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val api: EinoApi,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun searchMovies(query: String) = flowOf(
        try {
            Result.Success(api.searchMovies(query, dataStoreManager.getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    ).flowOn(Dispatchers.IO)

    suspend fun getMoviesByStatus(status: String) = flowOf(
        try {
            Result.Success(api.getMoviesByStatus(status, dataStoreManager.getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    ).flowOn(Dispatchers.IO)

    suspend fun getMovieById(movieId: String) = flowOf(
        try {
            Result.Success(api.getMovieById(movieId, dataStoreManager.getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    ).flowOn(Dispatchers.IO)

    suspend fun addMovie(movie: MovieRequest) = flowOf(
        try {
            Result.Success(api.addMovie(movie, dataStoreManager.getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    ).flowOn(Dispatchers.IO)

    suspend fun editMovie(id: String, movie: MovieRequest) = flowOf(
        try {
            Result.Success(api.updateMovie(id, movie, dataStoreManager.getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    ).flowOn(Dispatchers.IO)

    suspend fun deleteMovie(id: String) = flowOf(
        try {
            Result.Success(api.deleteMovie(id, dataStoreManager.getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    ).flowOn(Dispatchers.IO)
}