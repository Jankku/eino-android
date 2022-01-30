package com.jankku.eino.network.response.profile

data class ProfileResponse(
    val registration_date: String,
    val user_id: String,
    val username: String,
    val stats: Stats
)