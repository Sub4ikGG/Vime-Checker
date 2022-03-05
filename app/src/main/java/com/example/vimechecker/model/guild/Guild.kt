package com.example.vimechecker.model.guild

data class Guild(
    val avatar_url: String,
    val color: String,
    val created: Int,
    val id: Int,
    val level: Int,
    val levelPercentage: Double,
    val members: List<Member>,
    val name: String,
    val perks: Perks,
    val tag: String,
    val totalCoins: Int,
    val totalExp: Int,
    val web_info: Any
)