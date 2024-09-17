package com.example.testescomunicacao.BLE.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.cardioid.cardioid_ble.ble.BluetoothUtils
import com.cardioid.cardioid_ble.ble.models.ECG
import com.cardioid.cardioid_ble.ble.models.HandsOn
import com.cardioid.cardioid_ble.ble.models.HeartRate
import com.example.testescomunicacao.BLE.receiver.BleBroadcastReceiver
import com.example.testescomunicacao.BLE.Fragment.charts.EcgChartFragment
import com.example.testescomunicacao.BLE.Fragment.charts.HandsOnFragment
import com.example.testescomunicacao.BLE.ViewModel.AcquisitionViewModel
import com.example.testescomunicacao.BLE.ViewModel.ScanBleViewModel
import com.example.testescomunicacao.BLE.service.BluetoothLeService
import com.example.testescomunicacao.R
import com.example.testescomunicacao.communication.*
import com.example.testescomunicacao.databinding.FragmentDeviceBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class DeviceFragment : AcquisitionFragment(), DialogFileCallback {
    private var _binding: FragmentDeviceBinding? = null
    private val binding get() = _binding!!
    private var fileName: String? = null

    private lateinit var viewModelAc: AcquisitionViewModel
    private var isReceiverRegistered = false

    private var isServiceBound = false
    lateinit var viewModelBle: ScanBleViewModel

    lateinit var ecgChartFragment: EcgChartFragment
    lateinit var handsOnFragment: HandsOnFragment

    override val bluetoothLeReceiver: BleBroadcastReceiver = object : BleBroadcastReceiver() {
        override fun onStateChange(context: Context, state: String) {

            Log.e("-- DeviceF", "onStateChange, state: ${state}")
            if(state == getString(R.string.ble_state_connected)){
                this.cardioWheelECG.isMovesense =
                    bluetoothServiceConnection.bluetoothService?.isMovesense()?: false
                notify.add(Pair(
                    BluetoothUtils.STANDARD.SERVICES.HEART_RATE,
                    BluetoothUtils.STANDARD.CHARACTERISTICS.HEART_RATE)
                )
                notify.add(Pair(
                    BluetoothUtils.CARDIO_WHEEL.SERVICES.MAIN,
                    BluetoothUtils.CARDIO_WHEEL.CHARACTERISTICS.ECG)
                )
                handlerCreateFile(
                    getSampleRate(context),
                    getDeviceName(context),
                    fileName)
            }
        }

        override fun onCardioEcgDataAvailable(decode: ECG) {
            if (!isAdded) {
                return
            }
            ecgChartFragment.chartManager.displayChartData(decode.ecg)

            if (!isHandOn) {
                val communicationManager = CommunicationManager(
                    broadcastReceiverStrategy = BroadcastReceiverCommunicationStrategy(requireContext())
                )

                communicationManager.sendHandsOn(decode.hand, CommunicationStrategy.BROADCAST_RECEIVER)
                handsOnFragment.setHandsOnOld(decode.hand)
            }
            CoroutineScope(SupervisorJob()).launch{
                fileManager?.saveEcg(decode.ecg, decode.hand, ecgChartFragment.chartManager.last)
            }
        }

        override fun onCardioWheelHandsOnDataAvailable(decode: HandsOn) {
            // update UI
            val handsOnFragment = binding.handsOnFragment.getFragment<HandsOnFragment>()
            isHandOn = true

            val communicationManager = CommunicationManager(
                broadcastReceiverStrategy = BroadcastReceiverCommunicationStrategy(requireContext())
            )

            communicationManager.sendHandsOn(decode.handsOn, CommunicationStrategy.BROADCAST_RECEIVER)

            handsOnFragment.setHandsOn(decode.handsOn)
        }


        override fun onStandardHeartRateDataAvailable(decode: HeartRate) {
            if (!isAdded) return

            val heartRate = decode.heartRate.toString()
            Log.e("-- DeviceF", "decode.heartRate: ${decode.heartRate}")

            handsOnFragment.setHeartRate(heartRate)

            val communicationManager = CommunicationManager(
                broadcastReceiverStrategy = BroadcastReceiverCommunicationStrategy(requireContext()),
            )

            communicationManager.sendHeartRate(heartRate, CommunicationStrategy.BROADCAST_RECEIVER)
        }

        override fun onNotifyAction(poll: Pair<UUID, UUID>?) {
            Log.e("-- DeviceF", "onNotifyAction, poll: ${poll}")
            if(poll != null)
                bluetoothServiceConnection.bluetoothService?.notify(poll.first, poll.second)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("-- DeviceF", "onCreate")
        super.onCreate(savedInstanceState)
        fileName =  arguments?.getString(TAG_FILE_NAME, "")
        requireActivity().onBackPressedDispatcher.addCallback(this,callbackPressed)
        show = savedInstanceState?.getBoolean("SHOW", false) ?: false

        viewModelBle = ViewModelProvider(requireActivity())[ScanBleViewModel::class.java]
    }

    private fun unregisterBluetoothReceiver() {
        Log.e("-- DeviceF", "unregisterBluetoothReceiver")
        if (isReceiverRegistered) {
            try {
                requireActivity().unregisterReceiver(bluetoothLeReceiver)
                isReceiverRegistered = false
            } catch (e: IllegalArgumentException) {
                Log.e("-- DeviceFragment", "Receiver not registered: ${e.message}")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.e("-- DeviceF", "onCreateView")
        _binding = FragmentDeviceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("-- DeviceF", "onViewCreated")

        ecgChartFragment = binding.chartFragment.getFragment()
        handsOnFragment = binding.handsOnFragment.getFragment()

        showDialog()
    }

    private fun showDialog(){
        if(show){
            bluetoothServiceConnection.unBindService()
            Log.e("-- DeviceF", "---------- showDialog ----------")
            viewModelBle.devicesAdapter.setSelectedDevice(null)
            val dialogFileFragment = DialogFileFragment(this@DeviceFragment)
            dialogFileFragment.show(parentFragmentManager, tag)
            show = false
            callbackPressed.isEnabled = false

            Log.e("--DeviceF", "menu_Home clicked" +
                    "\n-->device: ${viewModelBle.devicesAdapter.mSelectedDevice.value}" +
                    "\n-->savedSelectedDevice: ${viewModelBle.devicesAdapter.savedSelectedDevice.value}" +
                    "\n-->selectedDevice: ${viewModelBle.devicesAdapter.getSelectedDevice()}")

        }
    }

    private val callbackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if(isEnabled){
                show = true
                if(checkPortrait()) showDialog()
                lockDeviceRotation(true)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.e("-- DeviceF", "onSaveInstanceState")
        outState.putBoolean("SHOW",show)
        outState.putBoolean("ROTATION",show)
        super.onSaveInstanceState(outState)
    }

    override fun onSubmit(fileName: String) {
        Log.e("-- DeviceF", "onSubmit")
        runBlocking {
            launch {
                fileManager?.renameFile(fileName)
            }
        }
        requireActivity().onBackPressed()
    }

    override fun onCancel() {
        Log.e("-- DeviceF", "onCancel")
        fileManager?.deleteFile()
        requireActivity().onBackPressed()
    }

    companion object {
        const val TAG_FILE_NAME = "FILE_NAME"
    }
}