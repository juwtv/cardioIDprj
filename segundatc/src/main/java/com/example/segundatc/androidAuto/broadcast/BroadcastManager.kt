package com.example.segundatc.androidAuto.broadcast

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.shared.SharedConstants.ACTION_AUTO_SCREEN_ACTIVATED
import com.example.shared.SharedConstants.SCREEN_NAME

object BroadcastManager {

    fun sendScreenActivatedBroadcast(context: Context, screenName: String) {
        Log.d("==> BroadcastManager", "Sending broadcast: $screenName")
        val intent = Intent(ACTION_AUTO_SCREEN_ACTIVATED)
        intent.apply {
            putExtra(SCREEN_NAME, screenName)
            setPackage("com.example.testescomunicacao")
        }
        context.sendBroadcast(intent)
    }
}
