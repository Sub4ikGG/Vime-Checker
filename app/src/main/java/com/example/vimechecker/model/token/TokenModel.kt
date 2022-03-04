package com.example.vimechecker.model.token

data class TokenModel(
    val limit: Int,
    val owner: Owner,
    val token: String,
    val type: String,
    val valid: Boolean
)