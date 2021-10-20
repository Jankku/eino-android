package com.jankku.eino.network

import com.jankku.eino.network.request.AddBookRequest
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

    @GET("list/books/all")
    suspend fun getAllBooks(@Header("Authorization") accessToken: String): BookListResponse

    @Headers(REQUEST_HEADERS)
    @POST("list/books/add")
    suspend fun addBook(
        @Body book: AddBookRequest,
        @Header("Authorization") accessToken: String
    ): AddBookResponse

    @Headers(REQUEST_HEADERS)
    @DELETE("list/books/delete/{id}")
    suspend fun deleteBook(
        @Path("id") id: String,
        @Header("Authorization") accessToken: String
    ): DeleteBookResponse

    /**
     *  Movie routes
     */

    @GET("list/movies/all")
    suspend fun getAllMovies(@Header("Authorization") accessToken: String): MovieListResponse
}
