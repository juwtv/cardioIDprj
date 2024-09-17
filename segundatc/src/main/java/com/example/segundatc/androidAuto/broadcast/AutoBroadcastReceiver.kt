package com.example.segundatc.androidAuto.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.shared.SharedConstants.AUTHENTICATION_NOK
import com.example.shared.SharedConstants.AUTHENTICATION_OK
import com.example.shared.SharedConstants.BOTH_HANDS_ON_WHEEL
import com.example.shared.SharedConstants.DROWSINESS
import com.example.shared.SharedConstants.ECG_NOK
import com.example.shared.SharedConstants.ECG_OK
import com.example.shared.SharedConstants.HIGH_HRV
import com.example.shared.SharedConstants.LONG_DRIVE
import com.example.shared.SharedConstants.LOW_HRV

class AutoBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        Log.d("==> AutoBroadcastReceiver", "intent: $intent")
        val response = intent.getStringExtra("response")
        Log.d("==> AutoBroadcastReceiver", "Received response: $response")

        // Process the received response and update the UI or trigger actions
        response?.let {
            when (it) {
                // ----- Authentication -----
                AUTHENTICATION_OK -> {}
                AUTHENTICATION_NOK -> {}
                // ----- Pop-Ups -----
                DROWSINESS -> {}
                BOTH_HANDS_ON_WHEEL -> {}
                LONG_DRIVE -> {}
                HIGH_HRV -> {}
                LOW_HRV -> {}
                // ----- ECG -----
                ECG_OK -> {}
                ECG_NOK -> {}
                else -> { /* Default or error handling */ }
            }
        }
    }
}
