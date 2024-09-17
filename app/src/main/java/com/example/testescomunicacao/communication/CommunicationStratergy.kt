package com.example.testescomunicacao.communication

import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.RemoteException
import android.util.Log
import android.view.View
import com.example.shared.SharedConstants
import com.example.shared.IMessageBoundService
import com.example.shared.IMyAidlInterface
import com.example.testescomunicacao.communication.CommunicationStrategy.Companion.showLogs
import com.example.testescomunicacao.fragments.socket.MessageSender


// ----- Communication interface -----
interface CommunicationStrategy {
    fun sendHeartRate(value: String, strategyType: String)
    abstract fun sendHandsOn(handsOn: Int, strategyType: String)

    companion object {
        const val AIDL = "AIDL"
        const val BOUND_SERVICE = "BOUND_SERVICE"
        const val BROADCAST_RECEIVER = "BROADCAST_RECEIVER"
        const val CONTENT_PROVIDER = "CONTENT_PROVIDER"
        const val INTENT = "INTENT"
        const val SOCKET = "SOCKET"

        const val showLogs = false

        // TODO: implementar ao receber os 4 sinais diferentes
        val ICON_RES_HANDS_ON = arrayOf(
            "hands_on_none",  // 0
            "hands_on_left",  // 1
            "hands_on_right", // 2
            "hands_on_both")  // 3
    }
}

// ----- Different communication stratergies -----
class AidlCommunicationStrategy(private val aidlService: IMyAidlInterface?) : CommunicationStrategy {
    override fun sendHeartRate(value: String, strategyType: String) {
        if (showLogs)
            Log.e("-- CommunicationStrategy", "Sending heart rate with $strategyType: $value")

        try {
            aidlService?.sendMessage(value)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun sendHandsOn(handsOn: Int, strategyType: String) {
        TODO("Not yet implemented")
    }
}

class BroadcastReceiverCommunicationStrategy(private val context: Context) : CommunicationStrategy {
    override fun sendHeartRate(value: String, strategyType: String) {
        //if (showLogs)
        //    Log.e("-- CommunicationStrategy", "Sending heart rate with $strategyType: $value")

        val intent = Intent().apply {
            //action = Constants.ACTION_SEND_MESSAGE_BD_HEART_RATE
            action = SharedConstants.ACTION_SEND_MESSAGE_BD
            putExtra("heart_rate", value)
            setPackage("com.example.segundatc")
        }
        context.sendBroadcast(intent)
    }

    override fun sendHandsOn(handsOn: Int, strategyType: String) {
        //if (showLogs)
        //    Log.e("-- CommunicationStrategy", "Sending handsOn $strategyType: [$handsOn]}")

        val intent = Intent().apply {
            //action = Constants.ACTION_SEND_MESSAGE_BD_HEART_RATE
            action = SharedConstants.ACTION_SEND_MESSAGE_BD
            putExtra("hands_on", handsOn.toString())
            setPackage("com.example.segundatc")
        }
        context.sendBroadcast(intent)
    }

}

class BoundServiceCommunicationStrategy(private val boundService: IMessageBoundService?) : CommunicationStrategy {
    override fun sendHeartRate(value: String, strategyType: String) {
        if (showLogs)
            Log.e("-- CommunicationStrategy", "Sending heart rate with $strategyType: $value")

        boundService?.sendMessage(value)
    }

    override fun sendHandsOn(handsOn: Int, strategyType: String) {
        TODO("Not yet implemented")
    }
}

class ContentProviderCommunicationStrategy(private val context: Context) : CommunicationStrategy {
    override fun sendHeartRate(value: String, strategyType: String) {
        if (showLogs)
            Log.e("-- CommunicationStrategy", "Sending heart rate with $strategyType: $value")

        val uri = Uri.parse("content://com.example.provider/heart_rate")
        val contentValues = ContentValues().apply {
            put("heart_rate", value)
        }
        context.contentResolver.insert(uri, contentValues)
    }

    override fun sendHandsOn(handsOn: Int, strategyType: String) {
        TODO("Not yet implemented")
    }
}


class IntentCommunicationStrategy(private val context: Context) : CommunicationStrategy {
    override fun sendHeartRate(value: String, strategyType: String) {
        if (showLogs)
            Log.e("-- CommunicationStrategy", "Sending heart rate with $strategyType: $value")

        /*val intent = Intent(context, SecondAppActivity::class.java).apply {
            putExtra("heart_rate", value)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)*/

        val intent = Intent()
        intent.component = ComponentName("com.example.segundatc", "com.example.segundatc.fragments.intent.IntentService")
        intent.action = SharedConstants.ACTION_SEND_MESSAGE_INTENT
        intent.putExtra("heart_rate", value)
        context.startService(intent)

    }

    override fun sendHandsOn(handsOn: Int, strategyType: String) {
        TODO("Not yet implemented")
    }
}

class SocketCommunicationStrategy(private val view: View) : CommunicationStrategy {
    override fun sendHeartRate(value: String, strategyType: String) {
        if (showLogs)
            Log.e("-- CommunicationStrategy", "Sending heart rate with $strategyType: $value")

        MessageSender(view).execute(value)
    }

    override fun sendHandsOn(handsOn: Int, strategyType: String) {
        TODO("Not yet implemented")
    }
}