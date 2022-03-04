package com.example.vimechecker.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vimechecker.data.repository.Repository
import com.example.vimechecker.model.projectOnline.OnlineModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel: ViewModel() {
    private var repository = Repository()
    var onlineLiveData = MutableLiveData<Response<OnlineModel>>()
    var fragmentLiveData = MutableLiveData<Fragment>()

    suspend fun getInfo() {
        onlineLiveData.postValue(repository.getOnline())
    }

    fun setFragment(fragment: Fragment) {
        fragmentLiveData.value = fragment
    }
}