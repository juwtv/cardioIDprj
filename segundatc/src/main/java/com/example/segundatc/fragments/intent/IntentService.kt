package com.example.segundatc.fragments.intent

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.segundatc.ViewModel.SharedViewModel

class IntentService : Service() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val receivedData = intent?.getStringExtra("intent_message")
        receivedData?.let {
            SharedViewModel.getInstance().setMessageIntent(it)
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}