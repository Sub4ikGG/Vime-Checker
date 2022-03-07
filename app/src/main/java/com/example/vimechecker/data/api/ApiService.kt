package com.example.vimechecker.data.api

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

    @GET("user/name/{name}")
    suspend fun getPlayerInfo(@Path("name") username: String): Response<PlayerModel>

    @GET("user/session/{id}")
    suspend fun getPlayerInfoWithOnline(@Path("id") id: Int): Response<PlayerOnline>

    @GET("/online/staff")
    suspend fun getAdmins(): Response<AdminModel>

    @GET("user/{id}/friends")
    suspend fun getPlayerFriends(@Path("id") id: Int): Response<PlayerFriends>

    @GET("/misc/token/{token}")
    suspend fun getToken(@Path("token") token: String): Response<TokenModel>

    @GET("user/{id}/matches")
    suspend fun getPlayerMatches(@Path("id") id: Int): Response<LastGamesModel>

    @GET("online")
    suspend fun getProjectOnline(): Response<OnlineModel>

    @GET("guild/get?")
    suspend fun getGuild(@Query("name") guild: String): Response<GuildModel>
}