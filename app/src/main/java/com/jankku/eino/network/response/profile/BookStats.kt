package com.jankku.eino.network.response.profile

data class BookStats(
    val count: String,
    val pages_read: String,
    val score_average: String,
    val score_distribution: List<ScoreDistribution>
)