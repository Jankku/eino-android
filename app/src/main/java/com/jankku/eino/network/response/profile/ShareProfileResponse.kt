package com.jankku.eino.network.response.profile

data class ShareProfileResponse(
    val results: List<Share>
)

data class Share(
    val share_id: String
)
