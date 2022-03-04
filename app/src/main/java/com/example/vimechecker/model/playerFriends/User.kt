package com.example.vimechecker.model.playerFriends

data class User(
    val guild: GuildX,
    val id: Int,
    val lastSeen: Int,
    val level: Int,
    val levelPercentage: Double,
    val playedSeconds: Int,
    val rank: String,
    val username: String
)