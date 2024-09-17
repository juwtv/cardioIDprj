package com.example.testescomunicacao.BLE.service

import android.bluetooth.le.ScanResult
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.example.testescomunicacao.BLE.service.BluetoothLeService.Companion.TAG_BLE_DEVICE

class BluetoothServiceConnection: ServiceConnection {
    var bluetoothService: BluetoothLeService? = null
    var contextRegister: Context? = null
    private var isServiceBound = false

    override fun onServiceConnected(className: ComponentName, service: IBinder) {
        val binder = service as BluetoothLeService.LocalBinder
        bluetoothService = binder.getService()
        Log.e("onServiceConnected", "onServiceConnected + \n" +
                "bluetoothService: $bluetoothService \n")
    }

    override fun onServiceDisconnected(className: ComponentName) {
        Log.e("onServiceDisconnected", "onServiceDisconnected")
        bluetoothService = null
    }

    fun bindService(context: Context, device: ScanResult) {
        contextRegister = context
        Log.e("BindService", "BindService")
        Intent(context, BluetoothLeService::class.java).also { intent ->
            intent.putExtra(TAG_BLE_DEVICE,device)
            context.bindService(intent, this, Context.BIND_AUTO_CREATE)
        }
    }

    fun unBindService() {
        contextRegister?.unbindService(this)
        bluetoothService = null
        Log.e("unBindService", "unBindService")
    }

    fun getService(): BluetoothLeService? = bluetoothService
}