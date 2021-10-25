package com.jankku.eino.network

import com.jankku.eino.network.request.BookRequest
import com.jankku.eino.network.request.LoginRequest
import com.jankku.eino.network.request.RegisterRequest
import com.jankku.eino.network.response.*
import com.jankku.eino.util.Constant.REQUEST_HEADERS
import retrofit2.http.*

interface EinoApiInterface {

    /**
     *  Auth routes
     */
    @Headers(REQUEST_HEADERS)
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    @Headers(REQUEST_HEADERS)
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    /**
     *  Book routes
     */
    @Headers(REQUEST_HEADERS)
    @GET("list/books/{status}")
    suspend fun getBooksByStatus(
        @Path("status") status: String,
        @Header("Authorization") accessToken: String
    ): BookListResponse

    @Headers(REQUEST_HEADERS)
    @GET("list/books/book/{id}")
    suspend fun getBookById(
        @Path("id") id: String,
        @Header("Authorization") accessToken: String
    ): BookListResponse

    @Headers(REQUEST_HEADERS)
    @POST("list/books/add")
    suspend fun addBook(
        @Body book: BookRequest,
        @Header("Authorization") accessToken: String
    ): AddBookResponse

    @Headers(REQUEST_HEADERS)
    @PUT("list/books/update/{id}")
    suspend fun updateBook(
        @Path("id") id: String,
        @Body book: BookRequest,
        @Header("Authorization") accessToken: String
    ): UpdateBookResponse

    @Headers(REQUEST_HEADERS)
    @DELETE("list/books/delete/{id}")
    suspend fun deleteBook(
        @Path("id") id: String,
        @Header("Authorization") accessToken: String
    ): DeleteBookResponse

    /**
     *  Movie routes
     */
    @Headers(REQUEST_HEADERS)
    @GET("list/movies/all")
    suspend fun getAllMovies(@Header("Authorization") accessToken: String): MovieListResponse
}
