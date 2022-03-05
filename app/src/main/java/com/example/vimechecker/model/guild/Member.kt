package com.example.vimechecker.model.guild

data class Member(
    val guildCoins: Int,
    val guildExp: Int,
    val joined: Int,
    val status: String,
    val user: User
)