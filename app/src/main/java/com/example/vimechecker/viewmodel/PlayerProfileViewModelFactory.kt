package com.example.vimechecker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class PlayerProfileViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PlayerProfileViewModel::class.java)) {
            return PlayerProfileViewModel() as T
        }
        throw IllegalArgumentException("ViewModel class not found.")
    }
}