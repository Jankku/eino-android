package com.jankku.eino.network.response.profile

data class MovieStats(
    val count: String,
    val score_average: String,
    val score_distribution: List<ScoreDistribution>,
    val watch_time: String
)