package com.example.vimechecker.model.admin

data class AdminModelItem(
    val guild: Guild,
    val id: Int,
    val lastSeen: Int,
    val level: Int,
    val levelPercentage: Double,
    val online: Online,
    val playedSeconds: Int,
    val rank: String,
    val username: String
)