package com.jankku.eino.data

import com.jankku.eino.network.EinoApiInterface
import com.jankku.eino.network.response.MovieListResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val api: EinoApiInterface,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun getAllMovies(): Flow<MovieListResponse> {
        return flowOf(api.getAllMovies(dataStoreManager.getAccessToken()))
    }
}