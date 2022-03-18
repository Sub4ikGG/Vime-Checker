package com.example.vimechecker.database

import android.provider.BaseColumns

object PlayerReaderContract {
    object PlayerEntry : BaseColumns {
        const val PLAYER_MODEL = "model"
        const val MODEL_NICKNAME = "nickname"
        const val MODEL_LEVEL = "level"
        const val MODEL_URL_AVATAR = "url_avatar"
    }
}