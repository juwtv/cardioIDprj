package com.example.testescomunicacao.BLE.receiver


import android.bluetooth.BluetoothGattCharacteristic
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import com.cardioid.cardioid_ble.ble.BluetoothUtils
import com.cardioid.cardioid_ble.ble.decoder.CardioWheelECG
import com.cardioid.cardioid_ble.ble.decoder.CardioWheelHeartRate
import com.cardioid.cardioid_ble.ble.decoder.CardioWheelHandsOn
import com.cardioid.cardioid_ble.ble.models.ECG
import com.cardioid.cardioid_ble.ble.models.HandsOn
import com.cardioid.cardioid_ble.ble.models.HeartRate
import com.example.testescomunicacao.BLE.ViewModel.AcquisitionViewModel
import com.example.testescomunicacao.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.LinkedList
import java.util.Queue
import java.util.UUID

abstract class BleBroadcastReceiver: BroadcastReceiver()  {
    private var cardioWheelHeartRate = CardioWheelHeartRate()
    private var cardioWheelHandsOn = CardioWheelHandsOn()
    var cardioWheelECG = CardioWheelECG()
    val notify: Queue<Pair<UUID, UUID>> = LinkedList()
    var isHandOn = false

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != BluetoothUtils.ACTION_DATA_AVAILABLE)
            Log.e("-- BleBR", "onReceive: ${intent.action.toString()}")

        when (intent.action) {
            BluetoothUtils.ACTION_DEVICE_CONNECTED    -> handlerOnStateChange(context, R.string.ble_state_connected)
            BluetoothUtils.ACTION_DEVICE_DISCONNECTED -> handlerOnStateChange(context,R.string.ble_state_disconnected)
            BluetoothUtils.ACTION_SERVICES_DISCOVERED -> onNotifyAction(notify.poll())
            BluetoothUtils.ACTION_DESCRIPTOR_WRITE    -> onNotifyAction(notify.poll())
            BluetoothUtils.ACTION_DATA_AVAILABLE      -> onDataAvailable(intent)
        }
    }
    @Suppress("DEPRECATION") // getParcelableExtra and characteristic.value only in 33
    private fun onDataAvailable(intent: Intent) {
        //Log.e("-- BleBR", "onDataAvailable")
        runBlocking {
            launch {
                val msg: Message? = intent.getParcelableExtra(BluetoothUtils.EXTRA_DATA)
                if (msg != null) {
                    val characteristic = msg.obj as BluetoothGattCharacteristic
                    characteristic.value = msg.data.getByteArray(AcquisitionViewModel.VALUE)
                    when (characteristic.uuid) {
                        BluetoothUtils.CARDIO_WHEEL.CHARACTERISTICS.ECG -> {
                            val ecgData = cardioWheelECG.decode(characteristic)
                            onCardioEcgDataAvailable(ecgData)
                        }
                        BluetoothUtils.CARDIO_WHEEL.CHARACTERISTICS.handsOn -> {
                            val handsOnData = cardioWheelHandsOn.decode(characteristic)
                            onCardioWheelHandsOnDataAvailable(handsOnData)
                        }
                        BluetoothUtils.STANDARD.CHARACTERISTICS.HEART_RATE -> {
                            val heartRateData = cardioWheelHeartRate.decode(characteristic)
                            onStandardHeartRateDataAvailable(heartRateData)
                        }
                    }
                }
            }
        }
    }

    private fun handlerOnStateChange(context: Context, @StringRes state: Int){
        Log.e("-- BleBR", "handlerOnStateChange ${context.getString(state)}")
        toastStatus(context, context.getString(state))
        onStateChange(context, context.getString(state))
    }

    abstract fun onStateChange(context: Context, state: String)
    abstract fun onCardioEcgDataAvailable(decode: ECG)
    abstract fun onCardioWheelHandsOnDataAvailable(decode: HandsOn)
    abstract fun onStandardHeartRateDataAvailable(decode: HeartRate)
    abstract fun onNotifyAction(poll: Pair<UUID, UUID>?)

    fun getSampleRate(context: Context):Int{
        return context.resources.getInteger(
            if(cardioWheelECG.isMovesense)
                R.integer.sample_rate_movesense else R.integer.sample_rate_cardio_wheel
        )
    }

    fun getDeviceName(context: Context):String{
        return context.getString(
            if(cardioWheelECG.isMovesense)
                R.string.ble_movesense else R.string.ble_cardio_wheel
        )
    }

    companion object{
        fun toastStatus(context: Context, state: String){
            val msg =context.getString(R.string.toast_ble_state, state)
            Toast.makeText(context,msg, Toast.LENGTH_SHORT).show()
        }
    }
}