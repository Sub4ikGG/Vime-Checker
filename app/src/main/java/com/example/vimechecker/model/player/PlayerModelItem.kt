package com.example.vimechecker.model.player

/*Объявление data class`a модели игрока,
перечисление полей модели*/
data class PlayerModelItem (
    val guild: Guild,
    val id: Int,
    val lastSeen: Int,
    val level: Int,
    val levelPercentage: Double,
    val playedSeconds: Int,
    val rank: String,
    val username: String
)















