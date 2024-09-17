package com.example.testescomunicacao.BLE.Fragment.charts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.example.testescomunicacao.BLE.io.ChartManager
import com.example.testescomunicacao.R
import com.example.testescomunicacao.databinding.FragmentEcgChartBinding

class EcgChartFragment : Fragment() {
    private var _binding: FragmentEcgChartBinding? = null
    private val binding get() = _binding!!
    var legend = true
    var chartManager: ChartManager = ChartManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateBufferSize()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEcgChartBinding.inflate(inflater, container, false)
        if (savedInstanceState != null) {
            chartManager.onLoad(requireContext(), label, binding.chart, savedInstanceState,legend)
        }
        else{
            chartManager.onCreate(requireContext(), label,binding.chart,legend)
        }
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(chartManager.onSave(outState))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.check_filter -> {
                item.isChecked = !item.isChecked
                chartManager.isFiltered = item.isChecked
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_filter, menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        chartManager.clear()
        _binding = null
    }

    private fun updateBufferSize(){
        val windowSeconds = resources.getInteger(R.integer.windowSeconds)
        chartManager.setSize(windowSeconds * 1000)
    }

    companion object {
        const val label = "ECG"
    }
}