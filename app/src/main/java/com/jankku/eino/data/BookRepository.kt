package com.jankku.eino.data

import com.jankku.eino.network.EinoApi
import com.jankku.eino.network.request.BookRequest
import com.jankku.eino.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val api: EinoApi,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun getBooksByStatus(status: String) = flowOf(
        try {
            Result.Success(api.getBooksByStatus(status, dataStoreManager.getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    ).flowOn(Dispatchers.IO)

    suspend fun getBookById(bookId: String) = flowOf(
        try {
            Result.Success(api.getBookById(bookId, dataStoreManager.getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    ).flowOn(Dispatchers.IO)

    suspend fun addBook(book: BookRequest) = flowOf(
        try {
            Result.Success(api.addBook(book, dataStoreManager.getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    ).flowOn(Dispatchers.IO)

    suspend fun editBook(id: String, book: BookRequest) = flowOf(
        try {
            Result.Success(api.updateBook(id, book, dataStoreManager.getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    ).flowOn(Dispatchers.IO)

    suspend fun deleteBook(id: String) = flowOf(
        try {
            Result.Success(api.deleteBook(id, dataStoreManager.getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    ).flowOn(Dispatchers.IO)
}