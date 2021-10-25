package com.jankku.eino.data

import com.jankku.eino.data.DataStoreManager.Companion.ACCESS_TOKEN
import com.jankku.eino.network.EinoApiInterface
import com.jankku.eino.network.request.BookRequest
import com.jankku.eino.network.response.AddBookResponse
import com.jankku.eino.network.response.DeleteBookResponse
import com.jankku.eino.network.response.UpdateBookResponse
import com.jankku.eino.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val api: EinoApiInterface,
    private val dataStoreManager: DataStoreManager
) {
    private suspend fun getAccessToken() = "Bearer ${dataStoreManager.getString(ACCESS_TOKEN)}"

    suspend fun getBooksByStatus(status: String) = flowOf(
        try {
            Result.Success(api.getBooksByStatus(status, getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    )

    suspend fun getBookById(bookId: String) = flowOf(
        try {
            Result.Success(api.getBookById(bookId, getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    )

    suspend fun addBook(book: BookRequest): Flow<Result<AddBookResponse>> = flowOf(
        try {
            Result.Success(api.addBook(book, getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    )

    suspend fun editBook(id: String, book: BookRequest): Flow<Result<UpdateBookResponse>> = flowOf(
        try {
            Result.Success(api.updateBook(id, book, getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    )

    suspend fun deleteBook(id: String): Flow<Result<DeleteBookResponse>> = flowOf(
        try {
            Result.Success(api.deleteBook(id, getAccessToken()))
        } catch (e: Exception) {
            Result.Error(e.message)
        }
    )
}