package com.example.vimechecker.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vimechecker.model.achievement.Achievements
import com.example.vimechecker.model.achievement.player.PlayerAchievement
import com.example.vimechecker.retrofit.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class AchievementsViewModel: ViewModel() {
    private val repository = Repository()
    val serverAchievements = MutableLiveData<Response<Achievements>>()
    val playerAchievements = MutableLiveData<Response<PlayerAchievement>>()

    fun getAchievements(server: Boolean, id: Int? = 0) {
        viewModelScope.launch(Dispatchers.IO) {
            if(server) serverAchievements.postValue(repository.getServerAchievements())
            else playerAchievements.postValue(repository.getPlayerAchievements(id))
        }
    }
}