package com.example.vimechecker.retrofit.repository

import com.example.vimechecker.model.achievement.Achievements
import com.example.vimechecker.model.achievement.player.PAchievement
import com.example.vimechecker.model.achievement.player.PlayerAchievement
import com.example.vimechecker.retrofit.api.RetrofitInstance
import com.example.vimechecker.model.admin.AdminModel
import com.example.vimechecker.model.guild.GuildModel
import com.example.vimechecker.model.lastgame.LastGamesModel
import com.example.vimechecker.model.player.PlayerModel
import com.example.vimechecker.model.playerFriends.PlayerFriends
import com.example.vimechecker.model.playerOnline.PlayerOnline
import com.example.vimechecker.model.projectOnline.OnlineModel
import com.example.vimechecker.model.token.TokenModel
import retrofit2.Response

class Repository {
    suspend fun getPlayerInfo(s: String): Response<PlayerModel> {
        return RetrofitInstance.API.getPlayerInfo(s)
    }

    suspend fun getPlayerInfoWithOnline(i: Int): Response<PlayerOnline> {
        return RetrofitInstance.API.getPlayerInfoWithOnline(i)
    }

    suspend fun getPlayerFriends(i: Int): Response<PlayerFriends> {
        return RetrofitInstance.API.getPlayerFriends(i)
    }

    suspend fun getToken(token: String): Response<TokenModel> {
        return RetrofitInstance.API.getToken(token)
    }

    suspend fun getPlayerMatches(id: Int): Response<LastGamesModel> {
        return RetrofitInstance.API.getPlayerMatches(id)
    }

    suspend fun getAdmins(): Response<AdminModel> {
        return RetrofitInstance.API.getAdmins()
    }

    suspend fun getOnline(): Response<OnlineModel> {
        return RetrofitInstance.API.getProjectOnline()
    }

    suspend fun getGuild(guild: String): Response<GuildModel> {
        return RetrofitInstance.API.getGuild(guild)
    }

    suspend fun getServerAchievements(): Response<Achievements> {
        return RetrofitInstance.API.getServerAchievements()
    }

    suspend fun getPlayerAchievements(id: Int?): Response<PlayerAchievement> {
        return RetrofitInstance.API.getPlayerAchievements(id)
    }
}