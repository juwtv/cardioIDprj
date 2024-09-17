package com.example.testescomunicacao.BLE.Fragment

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.cardioid.cardioid_ble.ble.BluetoothUtils
import com.example.testescomunicacao.BLE.service.BluetoothLeService
import com.example.testescomunicacao.BLE.service.BluetoothServiceConnection
import com.example.testescomunicacao.BLE.io.ECGFileManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


abstract class AcquisitionFragment : Fragment() {
    var fileManager: ECGFileManager? = null
    var device: ScanResult? = null
    var show: Boolean = false
    abstract val bluetoothLeReceiver: BroadcastReceiver

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION") // getParcelableExtra and characteristic.value only in 33
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        lockDeviceRotation(savedInstanceState?.getBoolean("ROTATION", false) ?: false)
        device = arguments?.getParcelable(BluetoothLeService.TAG_BLE_DEVICE)
        Log.e("--AcquisitionFragment", "onCreate " +
                "\ndevice: ${device?.device?.name}")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("--AcquisitionFragment", "onViewCreated")
        if (bluetoothServiceConnection.bluetoothService == null) {
            bluetoothServiceConnection.bindService(requireContext(), device!!)
        }
        //requireContext().registerReceiver(bluetoothLeReceiver, makeGattUpdateIntentFilter())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(bluetoothLeReceiver, makeGattUpdateIntentFilter(), Context.RECEIVER_NOT_EXPORTED)
            Log.e("-- AcquisitionFragment", "registerReceiver 1")
        } else {
            requireContext().registerReceiver(bluetoothLeReceiver, makeGattUpdateIntentFilter())
            Log.e("-- AcquisitionFragment", "registerReceiver 2")
        }
    }

    fun stopService(): Boolean {
        Log.e("-- AcquisitionFragment", "stopService")
        if(bluetoothServiceConnection.bluetoothService == null) return false
        bluetoothServiceConnection.unBindService()
        return true
    }

    override fun onDestroy() {
        Log.e("-- AcquisitionFragment", "onDestroy")
        super.onDestroy()
        requireContext().unregisterReceiver(bluetoothLeReceiver)
        if (isRemoving) {
            if(bluetoothServiceConnection.bluetoothService != null){
                bluetoothServiceConnection.unBindService()
            }
            lockDeviceRotation(true)
        }
    }

    @SuppressLint("SourceLockedOrientationActivity") // lock orientation cause bad user experience
    fun lockDeviceRotation(value: Boolean) {
        if (value) {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_USER
        }
    }

    fun checkPortrait(): Boolean {
        return requireActivity().resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    fun handlerCreateFile(samplingRate: Int, deviceName: String, fileName: String?){
        fileManager = ECGFileManager(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)!!,
            samplingRate
        )
        runBlocking {
            launch {
                if(fileName != null) {
                    fileManager?.createFile(fileName,deviceName)
                }
            }
        }
    }

    //    fun makeGattUpdateIntentFilter(): IntentFilter {
//        return IntentFilter().apply {
//            addAction(BluetoothUtils.ACTION_DEVICE_CONNECTED)
//            addAction(BluetoothUtils.ACTION_DEVICE_DISCONNECTED)
//            addAction(BluetoothUtils.ACTION_SERVICES_DISCOVERED)
//            addAction(BluetoothUtils.ACTION_DESCRIPTOR_WRITE)
//            addAction(BluetoothUtils.ACTION_DATA_AVAILABLE)
//        }
//    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        val bluetoothServiceConnection = BluetoothServiceConnection()

        fun makeGattUpdateIntentFilter(): IntentFilter {
            val intentFilter = IntentFilter().apply {
                addAction(BluetoothUtils.ACTION_DEVICE_CONNECTED)
                addAction(BluetoothUtils.ACTION_DEVICE_DISCONNECTED)
                addAction(BluetoothUtils.ACTION_SERVICES_DISCOVERED)
                addAction(BluetoothUtils.ACTION_DESCRIPTOR_WRITE)
                addAction(BluetoothUtils.ACTION_DATA_AVAILABLE)
            }
            return intentFilter
        }
    }
}