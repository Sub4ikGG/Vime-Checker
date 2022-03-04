package com.example.vimechecker.model.playerFriends

data class Friend(
    val guild: Guild?,
    var id: Int,
    val lastSeen: Int,
    val level: Int,
    val levelPercentage: Double,
    val playedSeconds: Int,
    val rank: String,
    var username: String
)