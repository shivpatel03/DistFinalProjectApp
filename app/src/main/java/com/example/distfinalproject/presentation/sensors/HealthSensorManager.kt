package com.example.distfinalproject.presentation.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import com.example.distfinalproject.presentation.HealthData
import com.example.distfinalproject.presentation.network.SocketManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HealthSensorManager(private val context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    private val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val socketManager = SocketManager()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var dataCollectionJob: Job? = null

    private val _heartRate = MutableStateFlow(0f)
    val heartRate: StateFlow<Float> = _heartRate

    private val _steps = MutableStateFlow(0f)
    val steps: StateFlow<Float> = _steps

    private val deviceId = android.provider.Settings.Secure.getString(
        context.contentResolver,
        android.provider.Settings.Secure.ANDROID_ID
    )
    private val userId = "user_${deviceId.take(8)}"
    private val username = generateDeviceName()

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {
                Sensor.TYPE_HEART_RATE -> _heartRate.value = event.values[0]
                Sensor.TYPE_STEP_COUNTER -> _steps.value = event.values[0]
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        }
    }

    private fun generateDeviceName(): String {
        val manufacturer = Build.MANUFACTURER.capitalize()
        val model = Build.MODEL.capitalize()
        val deviceNumber = deviceId.take(4)
        return "$manufacturer $model ($deviceNumber)"
    }

    private fun String.capitalize(): String {
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
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

        // start data collection
        dataCollectionJob = coroutineScope.launch {
            var connected = false
            while (!connected && isActive) {
                try {
                    socketManager.connect()
                    connected = true
                    Log.d("HealthSensorManager", "Connected to server with username: $username")
                } catch (e: Exception) {
                    Log.e("HealthSensorManager", "Failed to connect: ${e.message}")
                    delay(5000) // retry
                }
            }

            // Periodic data sending
            while (isActive) {
                try {
                    if (!socketManager.isConnected()) {
                        socketManager.connect()
                    }
                    sendDataToServer()
                    delay(3000) // end every 3 seconds
                } catch (e: Exception) {
                    Log.e("HealthSensorManager", "Error in data collection loop: ${e.message}")
                    delay(5000) // retry
                }
            }
        }
    }

    private suspend fun sendDataToServer() {
        val healthData = HealthData(
            userId = userId,
            username = username,
            deviceId = deviceId,
            heartRate = _heartRate.value,
            steps = _steps.value
        )

        Log.d("HealthSensorManager", "Sending data: $healthData")
        try {
            socketManager.sendData(healthData)
        } catch (e: Exception) {
            Log.e("HealthSensorManager", "Error sending data: ${e.message}")
            throw e
        }
    }

    fun stopMonitoring() {
        Log.d("HealthSensorManager", "Stopping monitoring for device: $deviceId")
        sensorManager.unregisterListener(sensorListener)
        dataCollectionJob?.cancel()
        coroutineScope.launch {
            socketManager.disconnect()
        }
    }

    fun simulateSteps(steps: Float) {
        _steps.value += steps
        coroutineScope.launch {
            try {
                sendDataToServer()
            } catch (e: Exception) {
                Log.e("HealthSensorManager", "Error simulating steps: ${e.message}")
            }
        }
    }
}
