package com.example.testescomunicacao.BLE.Fragment

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.cardioid.cardioid_ble.ble.scan.BluetoothLeScan
import com.example.testescomunicacao.BLE.service.BluetoothLeService
import com.example.testescomunicacao.BLE.adapter.SelectDevice
import com.example.testescomunicacao.BLE.ViewModel.ScanBleViewModel
import com.example.testescomunicacao.MainActivity
import com.example.testescomunicacao.R
import com.example.testescomunicacao.databinding.FragmentScanBleBinding

class ScanBleFragment : Fragment() {
    private var _binding: FragmentScanBleBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: ScanBleViewModel
    private lateinit var bluetoothLeScan: BluetoothLeScan
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.e("ScanBleFragment", "onCreateView: ")
        _binding = FragmentScanBleBinding.inflate(inflater, container, false)

        viewModel = ScanBleViewModel.getInstance()

        bluetoothLeScan = BluetoothLeScan(requireContext(),viewModel)
        bluetoothLeScan.isScanning.observe( viewLifecycleOwner) { onScanning(it) }

        binding.devicesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.devicesRecyclerView.adapter = viewModel.devicesAdapter
        viewModel.devicesAdapter.mSelectedDevice.observe( viewLifecycleOwner) { onSelectDevice(it) }
        binding.refreshLayout.setOnRefreshListener { bluetoothLeScan.startScan(TIME_SCAN.toLong()) }
        binding.refreshButton.setOnClickListener { bluetoothLeScan.startScan(TIME_SCAN.toLong()) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bluetoothLeScan.startScan(TIME_SCAN.toLong())
        Log.e("ScanBleFragment", "onViewCreated: bluetoothLeScan started")
    }

    override fun onDestroyView() {
        Log.e("ScanBleFragment", "onDestroyView: ")
        super.onDestroyView()
        _binding = null
    }

    private fun onScanning(isScanning: Boolean){
        binding.refreshLayout.isRefreshing = isScanning
        Log.e("ScanBleFragment", "onScanning out if, isScanning:$isScanning")
        if (isScanning) {
            Log.e("ScanBleFragment", "onScanning in if, isScanning:$isScanning")
            viewModel.devicesAdapter.clear()
        }
    }

    @SuppressLint("MissingPermission")
    fun onSelectDevice(selectDevice: SelectDevice?) {
        Log.e("ScanBleFragment", "onSelectDevice: $selectDevice"+
            "\n->selectedDevice: ${viewModel.devicesAdapter.getSelectedDevice()}" +
            "\n->savedSelectedDevice: ${viewModel.devicesAdapter.savedSelectedDevice.value}" +
            "\n->mSelectedDevice: ${viewModel.devicesAdapter.mSelectedDevice.value}")

        selectDevice?.let {
            val bundle = bundleOf(
                DeviceFragment.TAG_FILE_NAME to selectDevice.fileName,
                BluetoothLeService.TAG_BLE_DEVICE to selectDevice.scanResult
            )
            viewModel.devicesAdapter.mSelectedDevice.postValue(null)
            Log.e("-- ScanBleFragment","selectedDevice: ${viewModel.devicesAdapter.getSelectedDevice()}" +
                    "\nmSelectedDevice: ${viewModel.devicesAdapter.mSelectedDevice.value}")

            val deviceFragment = DeviceFragment().apply {
                arguments = bundle
            }

            // Replace the current fragment with the DeviceFragment
            (activity as? MainActivity)?.supportFragmentManager?.beginTransaction()?.apply {
                replace(R.id.frameLayoutContainer, deviceFragment)
                addToBackStack("DeviceFragment (from BLE)")
                commit()
            }
        }
    }

    companion object {
        const val TIME_SCAN = 5000
    }
}