package com.jankku.eino.network

import com.jankku.eino.network.request.*
import com.jankku.eino.network.response.auth.LoginResponse
import com.jankku.eino.network.response.auth.RefreshTokenResponse
import com.jankku.eino.network.response.auth.RegisterResponse
import com.jankku.eino.network.response.book.*
import com.jankku.eino.network.response.movie.*
import com.jankku.eino.network.response.profile.DeleteAccountResponse
import com.jankku.eino.network.response.profile.ProfileResponse
import com.jankku.eino.util.Constant.REQUEST_HEADERS
import retrofit2.http.*

interface EinoApi {

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
    @GET("list/books/search")
    suspend fun searchBooks(
        @Query("query") query: String,
        @Header("Authorization") accessToken: String
    ): BookSearchResponse

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
    @GET("list/movies/search")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Header("Authorization") accessToken: String
    ): MovieSearchResponse

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

    /**
     *  Profile routes
     */
    @Headers(REQUEST_HEADERS)
    @GET("profile")
    suspend fun getProfile(
        @Header("Authorization") accessToken: String
    ): ProfileResponse

    @Headers(REQUEST_HEADERS)
    @POST("profile/deleteaccount")
    suspend fun deleteAccount(
        @Body password: DeleteAccountRequest,
        @Header("Authorization") accessToken: String
    ): DeleteAccountResponse
}
