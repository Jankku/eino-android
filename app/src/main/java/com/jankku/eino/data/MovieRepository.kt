package com.jankku.eino.data

import com.jankku.eino.network.EinoApiInterface
import com.jankku.eino.network.request.MovieRequest
import com.jankku.eino.util.Result
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val api: EinoApiInterface,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun getMoviesByStatus(status: String) = flowOf(
        try {
            Result.Success(api.getMoviesByStatus(status, dataStoreManager.getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    )

    suspend fun getMovieById(movieId: String) = flowOf(
        try {
            Result.Success(api.getMovieById(movieId, dataStoreManager.getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    )

    suspend fun addMovie(movie: MovieRequest) = flowOf(
        try {
            Result.Success(api.addMovie(movie, dataStoreManager.getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    )

    suspend fun editMovie(id: String, movie: MovieRequest) = flowOf(
        try {
            Result.Success(api.updateMovie(id, movie, dataStoreManager.getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    )

    suspend fun deleteMovie(id: String) = flowOf(
        try {
            Result.Success(api.deleteMovie(id, dataStoreManager.getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    )
}