package com.example.segundatc.fragments.aidl

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.example.segundatc.ViewModel.SharedViewModel
import com.example.shared.IMyAidlInterface

class AIDLService : Service() {

    private val binder = object : IMyAidlInterface.Stub() {
        @Throws(RemoteException::class)
        override fun sendMessage(message: String) {
            SharedViewModel.getInstance().setMessageAIDL(message)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }
}
