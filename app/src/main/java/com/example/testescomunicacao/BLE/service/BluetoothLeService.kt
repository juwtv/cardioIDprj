package com.example.testescomunicacao.BLE.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.cardioid.cardioid_ble.ble.BluetoothLe
import com.example.testescomunicacao.MainActivity
import com.example.testescomunicacao.R
import java.util.*

class BluetoothLeService : Service() {
    private val binder = LocalBinder()
    private val bluetoothLe = BluetoothLe(this)

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothLeService = this@BluetoothLeService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    @SuppressLint("MissingPermission")
    override fun onBind(intent: Intent): IBinder {
        Log.e("-- BlESerivce", "onBind")

        val device =  intent.getParcelableExtra<ScanResult>(TAG_BLE_DEVICE)

        Log.e("-- BlESerivce", "device: $device " +
                "\n device?.device               : ${device?.device} " +
                "\n device?.device?.getAddress() : ${device?.device?.getAddress()} " +
                "\n address, name, bluetoothClass: ${device?.device?.address}, ${device?.device?.name}, ${device?.device?.bluetoothClass} " +
                "\n type, bondState, uuids       : ${device?.device?.type}, ${device?.device?.bondState}, ${device?.device?.uuids.toString()}")

        if(device != null && bluetoothLe.initialize()){
            Log.e("-- BlESerivce: onBind", "initialize")
            val boolValue = bluetoothLe.connect(device)
            Log.e("-- BlESerivce: onBind", "initialize: $boolValue")
        }
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.e("-- BlESerivce", "onUnbind")
        bluetoothLe.disconnect()
        return super.onUnbind(intent)
    }

    fun notify(service: UUID, characteristic: UUID): Boolean? {
        return try {
            Log.e("-- BlESerivce", "service: $service \ncharacteristic: $characteristic")
            bluetoothLe.notify(service,characteristic)
        } catch (e: Exception){
            Log.e("notify X - $characteristic", e.toString())
            false
        }
    }

    fun read(service: UUID, characteristic: UUID) {
        try {
            bluetoothLe.read(service,characteristic)
        } catch (e: Exception){
            Log.e("read - $characteristic", e.toString())
        }
    }

    fun write(service: UUID, characteristic: UUID, data: ByteArray): Boolean? {
        return try {
            bluetoothLe.write(service,characteristic, data)
        } catch (e: Exception){
            Log.e("write - $characteristic", e.toString())
            false
        }
    }

    fun isMovesense(): Boolean {
        return bluetoothLe.isMovesense()
    }

    companion object {
        const val TAG_BLE_DEVICE = "BLE_DEVICE"
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "BLEServiceChannel"
    }
}