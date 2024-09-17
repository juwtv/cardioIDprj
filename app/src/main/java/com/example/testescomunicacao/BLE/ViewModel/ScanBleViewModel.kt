package com.example.testescomunicacao.BLE.ViewModel


import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cardioid.cardioid_ble.ble.scan.BluetoothLeScanError
import com.cardioid.cardioid_ble.ble.scan.BluetoothLeScanListener
import com.example.testescomunicacao.BLE.adapter.DevicesAdapter
import com.example.testescomunicacao.BLE.adapter.SelectDevice


class ScanBleViewModel: ViewModel(), BluetoothLeScanListener {

    companion object {
        @Volatile private var instance: ScanBleViewModel? = null
        fun getInstance(): ScanBleViewModel {
            return instance ?: synchronized(this) {
                instance ?: ScanBleViewModel().also { instance = it }
            }
        }
    }

    val devicesAdapter: DevicesAdapter = DevicesAdapter()

    @SuppressLint("MissingPermission")
    override fun onDeviceScan(scanResult: ScanResult) {
        if(scanResult.device?.name?.lowercase()?.contains("movesense") == true){
            devicesAdapter.add(scanResult)
        }
        if(scanResult.device?.name?.lowercase()?.contains("cardio") == true){
            devicesAdapter.add(scanResult)
        }
    }
    override fun onErrorScan(scanError: BluetoothLeScanError) {
        Log.e("onErrorScan " + scanError.errorCode.toString(), scanError.message)
    }
    override fun onScanStop() {
        Log.i("ScanBle", "Stop ")
    }
}