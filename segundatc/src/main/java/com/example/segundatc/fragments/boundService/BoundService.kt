package com.example.segundatc.fragments.boundService

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.segundatc.ViewModel.SharedViewModel
import com.example.shared.IMessageBoundService

class BoundService : Service() {

    private val binder = object : IMessageBoundService.Stub() {
        override fun sendMessage(message: String) {
            SharedViewModel.getInstance().setMessageBS(message)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
}