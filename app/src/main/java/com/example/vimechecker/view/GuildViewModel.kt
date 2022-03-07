package com.example.vimechecker.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vimechecker.data.repository.Repository
import com.example.vimechecker.model.guild.GuildModel
import retrofit2.Response

class GuildViewModel: ViewModel() {
    private var repository = Repository()
    var guildLiveData = MutableLiveData<Response<GuildModel>>()

    suspend fun getInfo(guild: String) {
        guildLiveData.postValue(repository.getGuild(guild))
    }
}