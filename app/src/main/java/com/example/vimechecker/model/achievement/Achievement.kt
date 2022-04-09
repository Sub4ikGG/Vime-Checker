package com.example.vimechecker.model.achievement

data class Achievement(
    val description: List<String>,
    val id: Int,
    val reward: Int,
    var title: String
)