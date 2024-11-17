package com.example.distfinalproject.presentation

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
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

    Scaffold(
        timeText = { TimeText() },
        modifier = Modifier.fillMaxSize()
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            item {
                Text(
                    text = "Health Monitor",
                    style = MaterialTheme.typography.title2,
                    modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
                )
            }

            // Heart Rate Chip
            item {
                Chip(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    onClick = { /* Optional click action */ },
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("❤️")
                            Text(
                                if (heartRate > 0) "${heartRate.toInt()} BPM" else "-- BPM",
                                style = MaterialTheme.typography.body2
                            )
                        }
                    }
                )
            }

            item {
                Chip(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    onClick = {
                        // Debug: Increment steps on click
                        healthSensorManager.simulateSteps(steps + 10)
                    },
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("👣 Steps")
                            Text(
                                if (steps > 0) "${steps.toInt()}" else "0",
                                style = MaterialTheme.typography.body2
                            )
                        }
                    }
                )
            }

        }
    }
}