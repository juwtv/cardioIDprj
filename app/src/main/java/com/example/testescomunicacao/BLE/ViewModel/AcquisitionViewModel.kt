package com.example.testescomunicacao.BLE.ViewModel

import android.bluetooth.BluetoothGattCharacteristic
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Message
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cardioid.cardioid_ble.ble.BluetoothUtils
import com.cardioid.cardioid_ble.ble.decoder.CardioWheelECG
import com.cardioid.cardioid_ble.ble.decoder.CardioWheelHeartRate
import com.cardioid.cardioid_ble.ble.models.ECG
import com.cardioid.cardioid_ble.ble.models.HeartRate
import com.example.testescomunicacao.BLE.io.ChartManager
import com.example.testescomunicacao.BLE.io.ECGFileManager
import com.example.testescomunicacao.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class AcquisitionViewModel : ViewModel() {
    private var cardioWheelECG = CardioWheelECG()
    private var cardioWheelHeartRate = CardioWheelHeartRate()
    private val notify: Queue<Pair<UUID, UUID>> = LinkedList()
    var mHeartRate = MutableLiveData<String>()
    var mHand = MutableLiveData<Boolean>()
    var mNotifyAction = MutableLiveData<Pair<UUID, UUID>>()
    var chartManager: ChartManager = ChartManager()
    var fileWriter: ECGFileManager? = null
    var counter = 0
    var mEnroll = MutableLiveData(false)
    var progress = MutableLiveData(0)
    var enrollTime: Int = MainActivity.defaultEnrollTime

    private val _ecgData = MutableLiveData<ECG>()
    private val _heartRate = MutableLiveData<HeartRate>() // TODO: posso tirar?
    val ecgData: LiveData<ECG> get() = _ecgData
    val heartRate: LiveData<HeartRate> get() = _heartRate
    fun updateEcgData(decode: ECG) { _ecgData.value = decode }
    fun updateHeartRate(decode: HeartRate) { _heartRate.value = decode }

    val bluetoothLeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothUtils.ACTION_DEVICE_CONNECTED -> onStateChange(context, CONNECTED)
                BluetoothUtils.ACTION_DEVICE_DISCONNECTED -> onStateChange(context, DISCONNECTED)
                BluetoothUtils.ACTION_SERVICES_DISCOVERED -> mNotifyAction.postValue(notify.poll())
                BluetoothUtils.ACTION_DESCRIPTOR_WRITE -> mNotifyAction.postValue(notify.poll())
                BluetoothUtils.ACTION_DATA_AVAILABLE -> onDataAvailable(intent)
            }
        }
    }
    fun onStateChange(context: Context, state: String){
        if(state == CONNECTED){
            notify.add(Pair(BluetoothUtils.CARDIO_WHEEL.SERVICES.MAIN, BluetoothUtils.CARDIO_WHEEL.CHARACTERISTICS.ECG))
            notify.add(Pair(BluetoothUtils.STANDARD.SERVICES.HEART_RATE, BluetoothUtils.STANDARD.CHARACTERISTICS.HEART_RATE))
        }
        val msg = "The bluetooth device is ${state}!"
        Toast.makeText(context,msg, Toast.LENGTH_SHORT).show()
    }
    fun onDataAvailable(intent: Intent) {
        runBlocking {
            launch {
                val msg: Message? = intent.getParcelableExtra(BluetoothUtils.EXTRA_DATA)
                if (msg != null) {
                    val characteristic = (msg.obj as BluetoothGattCharacteristic)
                    characteristic.value = msg.data.getByteArray(VALUE)
                    when (characteristic.uuid) {
                        BluetoothUtils.CARDIO_WHEEL.CHARACTERISTICS.ECG -> {
                            val decode = cardioWheelECG.decode(characteristic)
                            chartManager.displayChartData(decode.ecg)
                            viewModelScope.launch(Dispatchers.IO) {
                                fileWriter?.saveEcg(decode.ecg, decode.hand, chartManager.last)
                            }
                            mHand.postValue(decode.hand.toString() == ON_HAND)
                            counter += decode.ecg.size
                            progress.postValue(counter)
                            if(counter >= (enrollTime * CARDIO_ECG_SAMPLING_RATE)){
                                if(mEnroll.value == false){
                                    mEnroll.postValue(true)
                                }
                            }
                        }
                        BluetoothUtils.STANDARD.CHARACTERISTICS.HEART_RATE -> {
                            mHeartRate.postValue(cardioWheelHeartRate.decode(characteristic).heartRate.toString())
                        }
                    }
                }
            }
        }
    }
    fun makeGattUpdateIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(BluetoothUtils.ACTION_DEVICE_CONNECTED)
            addAction(BluetoothUtils.ACTION_DEVICE_DISCONNECTED)
            addAction(BluetoothUtils.ACTION_SERVICES_DISCOVERED)
            addAction(BluetoothUtils.ACTION_DESCRIPTOR_WRITE)
            addAction(BluetoothUtils.ACTION_DATA_AVAILABLE)
        }
    }
    fun updateBufferSize(orientation: Int){
        val windowSeconds = if (orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 4
        chartManager.setSize(windowSeconds * CARDIO_ECG_SAMPLING_RATE)
    }

    companion object {
        const val ON_HAND = "1"
        const val VALUE = "value"
        const val CONNECTED = "connected"
        const val DISCONNECTED = "disconnected"
        const val CARDIO_ECG_SAMPLING_RATE = 1000
    }
}