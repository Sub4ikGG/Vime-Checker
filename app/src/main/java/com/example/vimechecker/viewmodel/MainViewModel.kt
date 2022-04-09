package com.example.vimechecker.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vimechecker.retrofit.repository.Repository
import com.example.vimechecker.model.projectOnline.OnlineModel
import retrofit2.Response

class MainViewModel: ViewModel() {
    private var repository = Repository()
    var onlineLiveData = MutableLiveData<Response<OnlineModel>>()
    var fragmentLiveData = MutableLiveData<Fragment>()

    suspend fun getInfo() {
        onlineLiveData.postValue(repository.getOnline())
    }
}