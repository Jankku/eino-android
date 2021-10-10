package com.jankku.eino.network

import com.jankku.eino.network.request.LoginRequest
import com.jankku.eino.network.request.RegisterRequest
import com.jankku.eino.network.response.BookListResponse
import com.jankku.eino.network.response.LoginResponse
import com.jankku.eino.network.response.MovieListResponse
import com.jankku.eino.network.response.RegisterResponse
import com.jankku.eino.util.Constant.REQUEST_HEADERS
import retrofit2.http.*

interface EinoApiInterface {
    @Headers(REQUEST_HEADERS)
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    @Headers(REQUEST_HEADERS)
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @GET("list/books/all")
    suspend fun getAllBooks(@Header("Authorization") accessToken: String): BookListResponse

    @GET("list/movies/all")
    suspend fun getAllMovies(@Header("Authorization") accessToken: String): MovieListResponse
}
