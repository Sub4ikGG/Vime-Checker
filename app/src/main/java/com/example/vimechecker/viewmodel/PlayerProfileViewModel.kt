package com.example.vimechecker.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vimechecker.retrofit.repository.Repository
import com.example.vimechecker.model.lastgame.LastGamesModel
import com.example.vimechecker.model.playerFriends.Friend
import com.example.vimechecker.model.playerOnline.PlayerOnline
import com.example.vimechecker.model.token.TokenModel
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

/*Да-да, нужно сделать 4 запроса,
чтобы получить информацию об игроке.
Таков Vimeworld-API*/

class PlayerProfileViewModel: ViewModel() {
    private val repository = Repository()
    private var id: Int? = null

    var playerLiveData = MutableLiveData<Response<PlayerOnline>>()
    var playerFriends = MutableLiveData<MutableList<Friend>>()
    var lastGamesLiveData = MutableLiveData<Response<LastGamesModel>>()
    var tokenInfo = MutableLiveData<Response<TokenModel>>()

    suspend fun getPlayerInfo(nickname: String): Boolean {
        if(id == null) {
            val dataForID = repository.getPlayerInfo(nickname)
            if (dataForID.body()?.isNotEmpty() == false) return false;

            id = dataForID.body()?.get(0)?.id
        }
        viewModelScope.launch(Dispatchers.IO) {
            val playerOnline = repository.getPlayerInfoWithOnline(id!!)
            playerLiveData.postValue(playerOnline)

            val pFriends = repository.getPlayerFriends(id!!)
            val friends = pFriends.body()?.friends

            if(friends?.isNotEmpty() == true) { // Filling array with anonymous
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

    suspend fun checkToken(token: String) {
        tokenInfo.postValue(repository.getToken(token))
    }
}