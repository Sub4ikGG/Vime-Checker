package com.example.vimechecker.model.lastgame

data class Matche(
    val date: Int,
    val duration: Int,
    val game: String,
    val id: String,
    val map: Any,
    val players: Int,
    val state: Int,
    val win: Boolean
)