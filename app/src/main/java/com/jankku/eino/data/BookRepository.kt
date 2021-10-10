package com.jankku.eino.data

import com.jankku.eino.data.DataStoreManager.Companion.ACCESS_TOKEN
import com.jankku.eino.network.EinoApiInterface
import com.jankku.eino.network.response.BookListResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val api: EinoApiInterface,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun getAllBooks(): Flow<BookListResponse> {
        val accessToken = "Bearer ${dataStoreManager.getString(ACCESS_TOKEN)}"
        return flowOf(api.getAllBooks(accessToken))
    }
}