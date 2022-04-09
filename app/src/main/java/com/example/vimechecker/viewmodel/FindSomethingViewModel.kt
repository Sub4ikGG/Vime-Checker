package com.example.vimechecker.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FindSomethingViewModel: ViewModel() {
    var currentObject = MutableLiveData<String>()

    fun setCurrentObject(nickname: String) {
        currentObject.value = nickname
    }
}