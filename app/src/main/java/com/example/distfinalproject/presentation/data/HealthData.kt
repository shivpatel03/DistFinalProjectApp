package com.example.distfinalproject.presentation.data

data class HealthData(
    val userId: String = "test_user",
    val heartRate: Float,
    val steps: Float,
    val timestamp: Long = System.currentTimeMillis()
)
