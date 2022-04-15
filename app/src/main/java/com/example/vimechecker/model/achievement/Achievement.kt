package com.example.vimechecker.model.achievement

data class Achievement(
    var description: List<String>,
    val id: Int,
    var reward: String,
    var title: String
)