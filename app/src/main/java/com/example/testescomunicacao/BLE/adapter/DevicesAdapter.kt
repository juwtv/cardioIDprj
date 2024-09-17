package com.example.testescomunicacao.BLE.adapter

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.testescomunicacao.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ViewHolder(val view : View) : RecyclerView.ViewHolder(view) {
    val deviceName : TextView = view.findViewById(R.id.deviceName)
    val deviceAddress : TextView = view.findViewById(R.id.deviceAddress)
}
data class SelectDevice(val scanResult: ScanResult, var fileName: String?)

/*
 * Adapter for recycler view (devices in scanBleFragment).
 * Bridge between UI component and data source
 * that helps us to fill data in UI component
 */
class DevicesAdapter :RecyclerView.Adapter<ViewHolder>(){

    private var devices: HashMap<String, ScanResult> = HashMap()
    private var selectedDevice: SelectDevice? = null
    val mSelectedDevice = MutableLiveData<SelectDevice?>()

    val savedSelectedDevice = MutableLiveData<SelectDevice?>()

    fun getSelectedDevice(): SelectDevice? {
        return selectedDevice
    }
    fun setSelectedDevice(selectedDevice: SelectDevice?) {
        this.selectedDevice = selectedDevice
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_view_item,parent,false)
        return ViewHolder(view)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val keyByIndex = devices.keys.elementAt(position)
        val scanResult = devices.getValue(keyByIndex)

        holder.deviceName.text = scanResult.device.name ?: "N/A"
        holder.deviceAddress.text = scanResult.device.address

        Log.e("-- DevicesAdapter","onBindViewHolder > scanResult.device.address: ${scanResult.device.address}")

        holder.view.setOnClickListener{
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
            sdf.timeZone = SimpleTimeZone(0, ID_TIMEZONE)
            selectedDevice = SelectDevice(scanResult, sdf.format(System.currentTimeMillis()))
            Log.e("-- DevicesAdapter","onBindViewHolder" +
                    "\nscanResult.device.address: ${scanResult.device.address}")
            mSelectedDevice.postValue(selectedDevice)
            savedSelectedDevice.postValue(selectedDevice)
            Log.e("-- DevicesAdapter","selectedDevice: $selectedDevice" +
                    "\nmSelectedDevice: ${mSelectedDevice.value}" +
                    "\nsavedSelectedDevice: ${savedSelectedDevice.value}")
        }
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    @SuppressLint("MissingPermission")
    fun add(scanResult: ScanResult){
        if(devices[scanResult.device.address] == null && scanResult.device.name != null){
            devices[scanResult.device.address] = scanResult
            val position = devices.keys.indexOf(scanResult.device.address)
            notifyItemInserted(position)
        }
    }

    fun clear(){
        val count = devices.size
        devices.clear()
        notifyItemRangeRemoved(0,count)
    }

    companion object {
        const val DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss"
        const val ID_TIMEZONE = "GMT"
    }
}