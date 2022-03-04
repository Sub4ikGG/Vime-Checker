package com.example.vimechecker.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vimechecker.data.repository.Repository
import com.example.vimechecker.model.admin.AdminModel
import retrofit2.Response

class AdminsListViewModel: ViewModel() {
    val repository = Repository()
    val adminsLiveData = MutableLiveData<Response<AdminModel>>()

    suspend fun getAdmins() {
        adminsLiveData.postValue(repository.getAdmins())
    }
}