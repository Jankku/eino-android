package com.jankku.eino.data

import com.jankku.eino.network.EinoApi
import com.jankku.eino.network.request.BookRequest
import com.jankku.eino.util.handleResponse
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val api: EinoApi,
    private val moshi: Moshi
) {
    suspend fun searchBooks(query: String) =
        handleResponse(api.searchBooks(query), moshi).flowOn(Dispatchers.IO)

    suspend fun getBooksByStatus(status: String) =
        handleResponse(api.getBooksByStatus(status), moshi).flowOn(Dispatchers.IO)

    suspend fun getBookById(id: String) =
        handleResponse(api.getBookById(id), moshi).flowOn(Dispatchers.IO)

    suspend fun addBook(book: BookRequest) =
        handleResponse(api.addBook(book), moshi).flowOn(Dispatchers.IO)

    suspend fun editBook(id: String, book: BookRequest) =
        handleResponse(api.updateBook(id, book), moshi).flowOn(Dispatchers.IO)

    suspend fun deleteBook(id: String) =
        handleResponse(api.deleteBook(id), moshi).flowOn(Dispatchers.IO)
}