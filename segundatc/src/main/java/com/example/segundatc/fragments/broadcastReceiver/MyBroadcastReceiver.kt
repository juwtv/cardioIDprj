package com.example.segundatc.fragments.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.segundatc.ViewModel.SharedViewModel

class MyBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private var instance: MyBroadcastReceiver? = null
        var isRegistered: Boolean = false

        fun getInstance(): MyBroadcastReceiver {
            if (instance == null) {
                instance = MyBroadcastReceiver()
            }
            return instance!!
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.hasExtra("message")) {
            val message = intent.getStringExtra("message")
            SharedViewModel.getInstance().setMessageBC(message ?: "")
        }
        if (intent.hasExtra("heart_rate")) {
            val heartRate = intent.getStringExtra("heart_rate")
            SharedViewModel.getInstance().setMessageHearRate(heartRate ?: "")
        }
        if(intent.hasExtra("hands_on")) {
            val handsOn = intent.getStringExtra("hands_on")
            if (handsOn != null) {
                SharedViewModel.getInstance().setHandsOn(handsOn ?: "")
            }
        }

    }
}