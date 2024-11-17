package com.example.distfinalproject.presentation.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.distfinalproject.presentation.data.HealthData
import com.example.distfinalproject.presentation.network.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HealthSensorManager(private val context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    private val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val _heartRate = MutableStateFlow(0f)
    val heartRate: StateFlow<Float> = _heartRate

    private val _steps = MutableStateFlow(0f)
    val steps: StateFlow<Float> = _steps

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {
                Sensor.TYPE_HEART_RATE -> {
                    _heartRate.value = event.values[0]
                    sendDataToServer()
                }
                Sensor.TYPE_STEP_COUNTER -> {
                    _steps.value = event.values[0]
                    sendDataToServer()
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // Handle accuracy changes if needed
        }
    }

    fun startMonitoring() {
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
    }

    fun stopMonitoring() {
        sensorManager.unregisterListener(sensorListener)
    }

    private fun sendDataToServer() {
        val healthData = HealthData(
            heartRate = _heartRate.value,
            steps = _steps.value
        )

        coroutineScope.launch {
            try {
                val response = NetworkManager.apiService.sendHealthData(healthData)
                if (!response.isSuccessful) {
                    // Handle error
                }
            } catch (e: Exception) {
                // Handle network error
            }
        }
    }

    fun simulateSteps(stepCount: Float) {
        _steps.value = stepCount
    }

}