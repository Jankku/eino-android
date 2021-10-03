package com.jankku.eino.network.response

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)