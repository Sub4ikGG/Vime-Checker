package com.example.vimechecker.model.token

data class Owner(
    val guild: Guild,
    val id: Int,
    val level: Int,
    val levelPercentage: Double,
    val playedSeconds: Int,
    val rank: String,
    val username: String
)