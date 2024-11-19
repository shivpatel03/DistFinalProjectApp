package com.example.distfinalproject.presentation.sensors

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.distfinalproject.presentation.data.HealthData
import com.example.distfinalproject.presentation.network.NetworkManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HealthSensorManager(private val context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    private val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private var dataCollectionJob: Job? = null
    private val SEND_INTERVAL = 10000L  // 10 seconds

    private val _heartRate = MutableStateFlow(0f)
    val heartRate: StateFlow<Float> = _heartRate

    private val _steps = MutableStateFlow(0f)
    val steps: StateFlow<Float> = _steps

    @SuppressLint("HardwareIds")
    private val deviceId = android.provider.Settings.Secure.getString(
        context.contentResolver,
        android.provider.Settings.Secure.ANDROID_ID
    )
    private val userId = "user_${deviceId.take(8)}"

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {
                Sensor.TYPE_HEART_RATE -> {
                    _heartRate.value = event.values[0]
                }
                Sensor.TYPE_STEP_COUNTER -> {
                    _steps.value = event.values[0]
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // Handle accuracy changes if needed
        }
    }

    fun startMonitoring() {
        // Start sensors
        heartRateSensor?.let {
            sensorManager.registerListener(
                sensorListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        stepCounterSensor?.let {
            sensorManager.registerListener(
                sensorListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        // Start periodic data sending
        dataCollectionJob = coroutineScope.launch {
            while (isActive) {
                sendDataToServer()
                delay(SEND_INTERVAL)
            }
        }
    }

    fun stopMonitoring() {
        sensorManager.unregisterListener(sensorListener)
        dataCollectionJob?.cancel()
    }

    private fun sendDataToServer() {
        val healthData = HealthData(
            userId = userId,
            deviceId = deviceId,
            heartRate = _heartRate.value,
            steps = _steps.value,
            timestamp = System.currentTimeMillis()
        )

        coroutineScope.launch {
            try {
                println("\n=== Sending Health Data ===")
                println("user ID: ${healthData.userId}")
                println("Device ID: ${healthData.deviceId}")
                println("Heart Rate: ${healthData.heartRate}")
                println("Steps: ${healthData.steps}")
                println("Time: ${java.util.Date(healthData.timestamp)}")

                val response = NetworkManager.apiService.sendHealthData(healthData)
                if (response.isSuccessful) {
                    println("✅ Data sent successfully")
                } else {
                    println("❌ Failed to send data: ${response.code()}")
                }
                println("========================\n")
            } catch (e: Exception) {
                println("\n❌ Error sending data: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun simulateSteps(stepCount: Float) {
        _steps.value = stepCount
    }
}