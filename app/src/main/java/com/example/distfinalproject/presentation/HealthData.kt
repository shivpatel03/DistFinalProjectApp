package com.example.distfinalproject.presentation

import java.io.Serializable

@Suppress("unused")
class HealthData : Serializable {
    @JvmField
    var userId: String = ""

    @JvmField
    var username: String = ""

    @JvmField
    var deviceId: String = ""

    @JvmField
    var heartRate: Float = 0f

    @JvmField
    var steps: Float = 0f

    @JvmField
    var timestamp: Long = System.currentTimeMillis()

    companion object {
        private const val serialVersionUID = 1234567890L
    }

    constructor()

    constructor(
        userId: String,
        username: String,
        deviceId: String,
        heartRate: Float,
        steps: Float,
        timestamp: Long = System.currentTimeMillis()
    ) {
        this.userId = userId
        this.username = username
        this.deviceId = deviceId
        this.heartRate = heartRate
        this.steps = steps
        this.timestamp = timestamp
    }

    override fun toString(): String {
        return "HealthData(userId='$userId', username='$username', deviceId='$deviceId', " +
                "heartRate=$heartRate, steps=$steps, timestamp=$timestamp)"
    }
}
