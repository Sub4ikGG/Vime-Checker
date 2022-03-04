package com.example.vimechecker.model.admin

data class Guild(
    val avatar_url: String,
    val color: String,
    val id: Int,
    val level: Int,
    val levelPercentage: Double,
    val name: String,
    val tag: String
)