package com.jankku.eino.data

import com.jankku.eino.data.DataStoreManager.Companion.ACCESS_TOKEN
import com.jankku.eino.network.EinoApiInterface
import com.jankku.eino.network.request.AddBookRequest
import com.jankku.eino.network.response.AddBookResponse
import com.jankku.eino.network.response.BookListResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val api: EinoApiInterface,
    private val dataStoreManager: DataStoreManager
) {
    private suspend fun getAccessToken() = "Bearer ${dataStoreManager.getString(ACCESS_TOKEN)}"

    suspend fun getAllBooks(): Flow<BookListResponse> = flowOf(api.getAllBooks(getAccessToken()))

    suspend fun addBook(book: AddBookRequest): Flow<AddBookResponse> =
        flowOf(api.addBook(book, getAccessToken()))
}