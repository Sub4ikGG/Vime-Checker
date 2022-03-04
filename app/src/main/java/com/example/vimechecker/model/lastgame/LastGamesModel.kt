package com.example.vimechecker.model.lastgame

data class LastGamesModel(
    val matches: List<Matche>,
    val request: Request,
    val user: User
)