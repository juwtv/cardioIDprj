package com.example.testescomunicacao.BLE.io


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import com.example.testescomunicacao.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


class ChartManager(private var bufferSize: Int = 0) {
    private var listOriginal :ArrayList<Entry> = ArrayList(bufferSize)
    private var listFiltered :ArrayList<Entry> = ArrayList(bufferSize)
    private var coroutineScope: CoroutineScope? = null
    private lateinit var filterData: FilterData
    private lateinit var dateFormat: SimpleDateFormat
    private var dataSet: LineDataSet? = null
    private var lineData: LineData? = null
    private var chart: LineChart? = null
    var last = -1
    var isFiltered = true
    var timeN = SystemClock.elapsedRealtime()

    fun onCreate(context: Context, label: String, chartResource: LineChart, legend: Boolean) {
        listOriginal.add(Entry(0F, 0F))
        listFiltered.add(Entry(0F, 0F))
        dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        initiate(context,label, chartResource, legend)
    }

    fun onLoad(context: Context, label: String, chartResource: LineChart, bundle: Bundle, legend: Boolean) {
        listOriginal = bundle.getParcelableArrayList(ORIGINAL)!!
        listFiltered = bundle.getParcelableArrayList(FILTERED)!!
        last = bundle.getInt(LAST)
        dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        initiate(context,label, chartResource, legend)
    }

    fun onSave(outState: Bundle): Bundle {
        outState.putInt(LAST, last)
        outState.putParcelableArrayList(ORIGINAL,listOriginal)
        outState.putParcelableArrayList(FILTERED, listFiltered)
        return outState
    }

    fun clear(){
        coroutineScope?.cancel()
        chart?.clear()
    }

    fun setSize (size: Int) {
        bufferSize = size
        listOriginal.ensureCapacity(bufferSize)
        listFiltered.ensureCapacity(bufferSize)
    }

    fun displayChartData(data: IntArray) {
        val auxOriginal = listOriginal
        val auxFiltered = listFiltered
        var count = last
        if (count == -1) {
            auxOriginal.removeAt(0)
            auxFiltered.removeAt(0)
        }
        for (i in data.indices) {
            val windowSize = count + 1
            if (auxOriginal.size+1  >= bufferSize) {
                auxOriginal.removeAt(0)
                auxFiltered.removeAt(0)
            }
            auxOriginal.add(Entry(windowSize.toFloat(), data[i].toFloat()))
            auxFiltered.add(Entry(windowSize.toFloat(), filterData.filterSignal(auxOriginal)))
            count++
        }
        last = count
        listOriginal = auxOriginal
        listFiltered = auxFiltered
        updateChart()
    }

    private fun initiate(context: Context, label: String,chartResource: LineChart, legend: Boolean){
        chart = chartResource
        dataSet = initiateDataSet(context,label)
        lineData = LineData(dataSet)
        chart = initiateChart(context, lineData!!, legend)
        filterData = FilterData(context, FILTER_NAME)
    }

    private fun initiateChart(context: Context, chartData: LineData, legend: Boolean): LineChart? {
        coroutineScope = CoroutineScope(Dispatchers.Main)
        val color = context.getColor(R.color.darkBlue)
        chart?.clear()
        chart?.axisRight?.isEnabled = false
        chart?.axisLeft?.isEnabled = false
        chart?.xAxis?.isEnabled = false
        chart?.xAxis?.textSize = 20F
        chart?.setGridBackgroundColor(color)
        chart?.isAutoScaleMinMaxEnabled = true
        chart?.setBackgroundColor(color)
        val windowSize = last + 1
        // When window size is bigger than buffer size, change the minimum of the XAxis;
        if (windowSize >= bufferSize) {
            chart?.xAxis?.axisMinimum = (windowSize - bufferSize).toFloat()
        } else {
            chart?.xAxis?.axisMinimum = 0f
        }
        chart?.description?.isEnabled = false
        chart?.setTouchEnabled(false)
        chart?.data = chartData
        chart?.invalidate()
        chart?.legend?.isEnabled = legend
        chart?.legend?.textSize = 18f
        chart?.legend?.textColor = Color.WHITE
        chart?.legend?.formSize = 12f
        return chart
    }

    private fun initiateDataSet(context: Context,label: String): LineDataSet {
        val color = context.getColor(R.color.colorAccent)
        val entries = ArrayList<Entry>(bufferSize)
        entries.add(Entry(0f, 0f))
        val dataSet = LineDataSet(entries, label)
        dataSet.color = color
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)
        dataSet.lineWidth = 1.5f
        return dataSet
    }

    private fun updateChart() {
        try {
            val count = last
            val windowSize = count + 1
            val time: String = dateFormat.format(Date(last.toLong()))
            dataSet?.values = if(isFiltered) listFiltered else listOriginal
            lineData?.dataSets?.get(0)?.label = time
            lineData?.notifyDataChanged()
            chart?.notifyDataSetChanged()

            if (windowSize >= bufferSize) {
                val axis = chart?.xAxis
                axis?.axisMinimum = (windowSize - bufferSize).toFloat()
            }
            chart?.invalidate()
        }catch (e:Exception){
            Log.e(TAG, e.toString())
        }
    }

    companion object {
        private const val ORIGINAL = "ORIGINAL"
        private const val FILTERED = "FILTERED"
        private const val LAST = "LAST"
        private const val FILTER_NAME = "cardio_filter"
        private const val TAG = "ChartManager"
    }
}