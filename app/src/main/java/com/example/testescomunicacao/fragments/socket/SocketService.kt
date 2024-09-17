package com.example.testescomunicacao.fragments.socket

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.ConcurrentLinkedQueue

class SocketService : Service() {

    private var ip: String? = null
    private val messageQueue = ConcurrentLinkedQueue<String>()
    @Volatile
    private var isRunning = false

    override fun onCreate() {
        super.onCreate()
        Log.d("-- SocketService --", "onCreate: entered")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("-- SocketService --", "onStartCommand: entered")
        intent?.let {
            when (it.action) {
                ACTION_START -> {
                    ip = it.getStringExtra(EXTRA_IP)
                    isRunning = true
                }
                ACTION_STOP -> {
                    isRunning = false
                }
                ACTION_SEND -> {
                    val message = it.getStringExtra(EXTRA_MESSAGE)
                    message?.let { msg -> sendMessage(msg) }
                }
                else -> {
                    Log.e("-- SocketService --", "onStartCommand: entered else")
                }
            }
        }

        return START_STICKY
    }

    private fun sendMessage(message: String) {
        if (isRunning) {
            ip?.let { ipAddress ->
                Thread {
                    try {
                        Socket(ipAddress, 7802).use { socket ->
                            PrintWriter(socket.getOutputStream(), true).use { pw ->
                                pw.println(message)
                                pw.flush()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            }
        } else {
            Log.e("SocketService", "Service is not running. Cannot send message: $message")
            sendBroadcastToFragment("Service is not running. Cannot send message: $message")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val ACTION_START = "com.example.testescomunicacao.services.START"
        const val ACTION_STOP = "com.example.testescomunicacao.services.STOP"
        const val ACTION_SEND = "com.example.testescomunicacao.services.SEND"
        const val EXTRA_IP = "com.example.testescomunicacao.services.EXTRA_IP"
        const val EXTRA_MESSAGE = "com.example.testescomunicacao.services.EXTRA_MESSAGE"
    }

    // send message back to the fragment
    private fun sendBroadcastToFragment(message: String) {
        val intent = Intent("com.example.testescomunicacao.SERVICE_MESSAGE")
        intent.putExtra("message", message)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}