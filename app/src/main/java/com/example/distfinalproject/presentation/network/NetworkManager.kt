package com.example.distfinalproject.presentation.network

import com.example.distfinalproject.presentation.data.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkManager {
    private const val BASE_URL = "http://10.0.2.2:3000/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
