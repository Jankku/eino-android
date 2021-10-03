package com.jankku.eino.network

import com.jankku.eino.network.request.LoginRequest
import com.jankku.eino.network.request.RegisterRequest
import com.jankku.eino.network.response.LoginResponse
import com.jankku.eino.network.response.RegisterResponse
import com.jankku.eino.util.Constant.CONTENT_TYPE_HEADER
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface EinoApiInterface {
    @Headers(CONTENT_TYPE_HEADER)
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    @Headers(CONTENT_TYPE_HEADER)
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse
}
