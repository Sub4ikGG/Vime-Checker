package com.example.vimechecker.room

import androidx.lifecycle.LiveData

class RequestRepository(private val requestDao: RequestDao) {
    val readAllData: LiveData<List<Request>> = requestDao.readAllData()

    suspend fun addPlayer(request: Request) {
        requestDao.addPlayer(request)
    }

    suspend fun upPlayer(request: Request) {
        requestDao.deletePlayer(request)
    }
}