package com.example.segundatc.androidAuto

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.car.app.CarAppService
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator
import com.example.segundatc.ViewModel.SharedViewModel
import com.example.segundatc.androidAuto.auth.ChooseDriverScreen
import com.example.segundatc.androidAuto.auth.LastSessionScreen
import com.example.segundatc.androidAuto.auth.UserPreferences
import com.example.segundatc.androidAuto.broadcast.BroadcastManager
import com.example.shared.SharedConstants
import com.example.shared.SharedConstants.ACTION_SEND_MESSAGE_AUTO

class AndroidAutoService : CarAppService() {

    private val sharedViewModel = SharedViewModel.getInstance()
    private lateinit var autoBroadcastReceiver: BroadcastReceiver
    private lateinit var context: Context

    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(): Session {
        return object : Session() {
            override fun onCreateScreen(intent: Intent): Screen {
                context = carContext

                val userPreferences = UserPreferences(carContext)
                val driverUuid = userPreferences.getCurrentDriver()?.uuid
                val driverName = userPreferences.getCurrentDriver()?.name

                return if (driverUuid == null) ChooseDriverScreen(carContext)
                else LastSessionScreen(carContext, driverName?: "")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        autoBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val response = intent.getStringExtra("response")
                Log.d("AndroidAutoService", "Received response: $response")

                response?.let {
                    sharedViewModel.setBroadcastResponse(it)
                    Log.e("setBroadcastResponse", "AndroidAutoService: ${it}")
                }
            }
        }

        val filter = IntentFilter(ACTION_SEND_MESSAGE_AUTO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(autoBroadcastReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(autoBroadcastReceiver, filter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BroadcastManager.sendScreenActivatedBroadcast(context, SharedConstants.TAG_NO_NOTIFICATION_SCREEN)
        unregisterReceiver(autoBroadcastReceiver)
    }
}