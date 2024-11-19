package com.example.distfinalproject.presentation

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.wear.compose.material.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.distfinalproject.presentation.sensors.HealthSensorManager

class MainActivity : ComponentActivity() {
    private lateinit var healthSensorManager: HealthSensorManager

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.BODY_SENSORS, false) -> {
                healthSensorManager.startMonitoring()
            }
            else -> {
                // Handle permission denied
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        healthSensorManager = HealthSensorManager(this)

        setContent {
            HealthMonitoringApp(healthSensorManager)
        }

        permissionLauncher.launch(arrayOf(
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.INTERNET
        ))
    }

    override fun onDestroy() {
        super.onDestroy()
        healthSensorManager.stopMonitoring()
    }
}

@Composable
fun HealthMonitoringApp(healthSensorManager: HealthSensorManager) {
    val heartRate by healthSensorManager.heartRate.collectAsState()
    val steps by healthSensorManager.steps.collectAsState()

    ScalingLazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Display current time
        item {
            TimeText()
        }

        // Heart Rate Chip
        item {
            Chip(
                onClick = { /* Optional click action */ },
                label = { Text("Heart Rate: $heartRate") },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        item {
            Chip(
                onClick = { healthSensorManager.simulateSteps(10f) },
                label = { Text("Steps: $steps") },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}