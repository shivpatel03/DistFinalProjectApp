package com.example.distfinalproject.presentation.data

data class HealthData(
    val userId: String,
    val deviceId: String,
    val heartRate: Float,
    val steps: Float,
    val timestamp: Long = System.currentTimeMillis()
)
