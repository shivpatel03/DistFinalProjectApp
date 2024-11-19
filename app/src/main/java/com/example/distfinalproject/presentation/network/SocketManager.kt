package com.example.distfinalproject.presentation.network

import com.example.distfinalproject.presentation.HealthData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

class SocketManager(
    private val host: String = "10.0.2.2",
    private val port: Int = 9999
) {
    private var socket: Socket? = null
    private var output: ObjectOutputStream? = null
    private var input: ObjectInputStream? = null
    private var isConnected = false

    suspend fun connect() = withContext(Dispatchers.IO) {
        if (isConnected) {
            println("Already connected")
            return@withContext
        }

        try {
            println("Connecting to server...")
            socket = Socket(host, port)

            output = ObjectOutputStream(socket?.getOutputStream())
            output?.flush()

            input = ObjectInputStream(socket?.getInputStream())

            isConnected = true
            println("Connected to server successfully")
        } catch (e: Exception) {
            println("Connection failed: ${e.message}")
            cleanup()
            throw e
        }
    }

    suspend fun sendData(data: HealthData) = withContext(Dispatchers.IO) {
        if (!isConnected) {
            println("Not connected, attempting to reconnect...")
            connect()
        }

        try {
            println("Sending data: $data")
            output?.writeObject(data)
            output?.flush()
            println("Data sent successfully")
        } catch (e: Exception) {
            println("Failed to send data: ${e.message}")
            cleanup()
            throw e
        }
    }

    private fun cleanup() {
        isConnected = false
        try {
            input?.close()
            output?.close()
            socket?.close()
        } catch (e: Exception) {
            println("Error in cleanup: ${e.message}")
        }
        input = null
        output = null
        socket = null
    }

    fun disconnect() {
        println("Disconnecting from server...")
        cleanup()
        println("Disconnected from server")
    }

    fun isConnected(): Boolean {
        return isConnected && socket?.isConnected == true && !socket?.isClosed!!
    }
}
