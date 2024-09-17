package com.example.segundatc.fragments.socket

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.segundatc.Constants.ACTION_MESSAGE_RECEIVED
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class MessageReceiverService : Service() {

    private var serverSocket: ServerSocket? = null
    private var isRunning = true

    override fun onCreate() {
        super.onCreate()
        startServer()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        if (serverSocket != null && !serverSocket!!.isClosed) {
            serverSocket!!.close();
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startServer() {
        Thread {
            try {
                //serverSocket = ServerSocket(7802)
                //serverSocket = ServerSocket(7802, 50, InetAddress.getByName("127.0.0.1"))
                serverSocket = ServerSocket().apply {
                    reuseAddress = true
                    bind(InetSocketAddress(InetAddress.getByName("127.0.0.1"), 7802), 50)
                }
                Log.d("-- debug: MessageReceiverService --", "Server started on port 7802")

                while (isRunning) {
                    val clientSocket: Socket = serverSocket!!.accept()
                    val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                    val message = reader.readLine()
                    Log.d("-- debug: MessageReceiverService --", "Received message: $message")
                    sendMessageToFragment(message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("-- debug: MessageReceiverService --", "Error: ${e.message}")
            } finally {
                if (serverSocket != null && !serverSocket!!.isClosed) {
                    try {
                        serverSocket!!.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("-- debug: MessageReceiverService --", "Error closing server socket: ${e.message}")
                    }
                }
            }
        }.start()
    }

    private fun sendMessageToFragment(message: String) {
        val intent = Intent(ACTION_MESSAGE_RECEIVED)
        intent.putExtra("message", message)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}
