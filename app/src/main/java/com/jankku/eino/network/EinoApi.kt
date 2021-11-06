package com.jankku.eino.network

import com.jankku.eino.network.request.*
import com.jankku.eino.network.response.auth.LoginResponse
import com.jankku.eino.network.response.auth.RefreshTokenResponse
import com.jankku.eino.network.response.auth.RegisterResponse
import com.jankku.eino.network.response.book.AddBookResponse
import com.jankku.eino.network.response.book.BookListResponse
import com.jankku.eino.network.response.book.DeleteBookResponse
import com.jankku.eino.network.response.book.UpdateBookResponse
import com.jankku.eino.network.response.movie.AddMovieResponse
import com.jankku.eino.network.response.movie.DeleteMovieResponse
import com.jankku.eino.network.response.movie.MovieListResponse
import com.jankku.eino.network.response.movie.UpdateMovieResponse
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

    @Headers(REQUEST_HEADERS)
    @POST("auth/refreshtoken")
    suspend fun refreshToken(@Body body: RefreshTokenRequest): RefreshTokenResponse

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
    @GET("list/movies/{status}")
    suspend fun getMoviesByStatus(
        @Path("status") status: String,
        @Header("Authorization") accessToken: String
    ): MovieListResponse

    @Headers(REQUEST_HEADERS)
    @GET("list/movies/movie/{id}")
    suspend fun getMovieById(
        @Path("id") id: String,
        @Header("Authorization") accessToken: String
    ): MovieListResponse

    @Headers(REQUEST_HEADERS)
    @POST("list/movies/add")
    suspend fun addMovie(
        @Body movie: MovieRequest,
        @Header("Authorization") accessToken: String
    ): AddMovieResponse

    @Headers(REQUEST_HEADERS)
    @PUT("list/movies/update/{id}")
    suspend fun updateMovie(
        @Path("id") id: String,
        @Body movie: MovieRequest,
        @Header("Authorization") accessToken: String
    ): UpdateMovieResponse

    @Headers(REQUEST_HEADERS)
    @DELETE("list/movies/delete/{id}")
    suspend fun deleteMovie(
        @Path("id") id: String,
        @Header("Authorization") accessToken: String
    ): DeleteMovieResponse
}
