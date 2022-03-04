package com.example.vimechecker.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vimechecker.data.repository.Repository
import com.example.vimechecker.model.lastgame.LastGamesModel
import com.example.vimechecker.model.lastgame.Matche
import com.example.vimechecker.model.playerFriends.Friend
import com.example.vimechecker.model.playerFriends.PlayerFriends
import com.example.vimechecker.model.playerOnline.PlayerOnline
import com.example.vimechecker.model.token.TokenModel
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class PlayerProfileViewModel: ViewModel() {
    private val repository = Repository()
    private var id: Int? = 0
    private var scope = CoroutineScope(Dispatchers.IO + CoroutineName("ViewModel"))
    var playerLiveData = MutableLiveData<Response<PlayerOnline>>()
    var playerFriends = MutableLiveData<MutableList<Friend>>()
    var tokenInfo = MutableLiveData<Response<TokenModel>>()
    var lastGamesLiveData = MutableLiveData<Response<LastGamesModel>>()

    suspend fun getPlayerInfo(nickname: String): Boolean {
        val dataForID = repository.getPlayerInfo(nickname)
        if(dataForID.body()?.isNotEmpty() == false) return false;

        id = dataForID.body()?.get(0)?.id
        scope.launch {
            val _playerOnline = repository.getPlayerInfoWithOnline(id!!)
            playerLiveData.postValue(_playerOnline)

            val _playerFriends = repository.getPlayerFriends(id!!)
            val friends = _playerFriends.body()?.friends
            if(friends?.isNotEmpty() == true) {
                val anonymous = friends.first().copy()
                anonymous.id = -1

                val array = friends as MutableList<Friend>
                while (array.size < 6) anonymous.let { array.add(it) }
                playerFriends.postValue(array)
            }

            val lastGames = repository.getPlayerMatches(id!!)
            lastGamesLiveData.postValue(lastGames)
        }

        return true
    }

    override fun onCleared() {
        super.onCleared()
    }

    suspend fun checkToken(token: String) {
        tokenInfo.postValue(repository.getToken(token))
    }
}