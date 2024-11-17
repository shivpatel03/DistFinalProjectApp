package com.example.distfinalproject.presentation.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/health-data")
    suspend fun sendHealthData(@Body data: HealthData): Response<Unit>
}
