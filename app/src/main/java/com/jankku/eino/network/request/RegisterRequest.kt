package com.jankku.eino.network.request

data class RegisterRequest(
    val username: String,
    val password: String,
    val password2: String
)