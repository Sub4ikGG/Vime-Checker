package com.example.vimechecker.retrofit.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.vimeworld.ru/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val API: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}