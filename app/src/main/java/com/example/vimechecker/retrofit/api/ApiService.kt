package com.example.vimechecker.retrofit.api

import com.example.vimechecker.model.achievement.Achievements
import com.example.vimechecker.model.achievement.player.PAchievement
import com.example.vimechecker.model.achievement.player.PlayerAchievement
import com.example.vimechecker.model.admin.AdminModel
import com.example.vimechecker.model.guild.GuildModel
import com.example.vimechecker.model.lastgame.LastGamesModel
import com.example.vimechecker.model.player.PlayerModel
import com.example.vimechecker.model.playerFriends.PlayerFriends
import com.example.vimechecker.model.playerOnline.PlayerOnline
import com.example.vimechecker.model.projectOnline.OnlineModel
import com.example.vimechecker.model.token.TokenModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("user/name/{name}?token=D4OepxCVeGLWdhfjwMKnbdoBLZoIsMB")
    suspend fun getPlayerInfo(@Path("name") username: String): Response<PlayerModel>

    @GET("user/session/{id}?token=D4OepxCVeGLWdhfjwMKnbdoBLZoIsMB")
    suspend fun getPlayerInfoWithOnline(@Path("id") id: Int): Response<PlayerOnline>

    @GET("/online/staff?token=D4OepxCVeGLWdhfjwMKnbdoBLZoIsMB")
    suspend fun getAdmins(): Response<AdminModel>

    @GET("user/{id}/friends?token=D4OepxCVeGLWdhfjwMKnbdoBLZoIsMB")
    suspend fun getPlayerFriends(@Path("id") id: Int): Response<PlayerFriends>

    @GET("/misc/token/{token}")
    suspend fun getToken(@Path("token") token: String): Response<TokenModel>

    @GET("user/{id}/matches?token=D4OepxCVeGLWdhfjwMKnbdoBLZoIsMB")
    suspend fun getPlayerMatches(@Path("id") id: Int): Response<LastGamesModel>

    @GET("online?token=D4OepxCVeGLWdhfjwMKnbdoBLZoIsMB")
    suspend fun getProjectOnline(): Response<OnlineModel>

    @GET("guild/get?")
    suspend fun getGuild(@Query("name") guild: String, @Query("token") token: String = "D4OepxCVeGLWdhfjwMKnbdoBLZoIsMB"): Response<GuildModel>

    @GET("misc/achievements?token=D4OepxCVeGLWdhfjwMKnbdoBLZoIsMB")
    suspend fun getServerAchievements(): Response<Achievements>

    @GET("/user/{id}/achievements?token=D4OepxCVeGLWdhfjwMKnbdoBLZoIsMB")
    suspend fun getPlayerAchievements(@Path("id") id: Int?): Response<PlayerAchievement>
}