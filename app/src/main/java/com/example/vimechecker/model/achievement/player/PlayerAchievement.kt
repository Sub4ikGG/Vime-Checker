package com.example.vimechecker.model.achievement.player

data class PlayerAchievement(
    val user: User,
    val achievements: List<PAchievement>
)