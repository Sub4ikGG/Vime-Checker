package com.example.vimechecker.model.guild

data class User(
    val id: Int,
    val lastSeen: Int,
    val level: Int,
    val levelPercentage: Double,
    val playedSeconds: Int,
    val rank: String,
    val username: String
)