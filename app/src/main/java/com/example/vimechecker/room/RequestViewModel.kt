package com.example.vimechecker.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class RequestViewModel(application: Application): AndroidViewModel(application) {
    val readAllData: LiveData<List<Request>>
    private val requestRepository: RequestRepository
    private val scope = CoroutineScope(Dispatchers.IO + CoroutineName("mReq-API"))

    init {
        val requestDao = AppDatabase.getDatabase(application).requestDao()
        requestRepository = RequestRepository(requestDao)
        readAllData = requestRepository.readAllData
    }

    fun addPlayer(request: Request) {
        scope.launch(Dispatchers.IO) {
            requestRepository.addPlayer(request)
        }
    }

    fun upPlayer(oldModel: Request, newModel: Request) {
        scope.launch(Dispatchers.IO) {
            requestRepository.upPlayer(oldModel)
            requestRepository.addPlayer(newModel)
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}