package com.example.vimechecker.model.lastgame

data class User(
    val guild: Any,
    val id: Int,
    val lastSeen: Int,
    val level: Int,
    val levelPercentage: Double,
    val playedSeconds: Int,
    val rank: String,
    val username: String
)