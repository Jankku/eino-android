package com.jankku.eino.network.response.auth

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)