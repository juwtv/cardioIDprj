package com.example.testescomunicacao.BLE.io

import android.content.Context
import com.example.testescomunicacao.BLE.model.Filter
import com.github.mikephil.charting.data.Entry
import com.google.gson.Gson
import java.util.*

class FilterData(val context: Context, private val filterName: String = "cardio_filter") {
    private var filter: Filter = getFilter()

    /**
     * Get Filter json and convert in object Filter
     */
    private fun getFilter(): Filter {
        val gson = Gson()
        val jsonData = context.resources.openRawResource(
            context.resources.getIdentifier(
                filterName,
                "raw", context.packageName
            )
        ).bufferedReader().use { it.readText() }
        return gson.fromJson<Filter>(jsonData, Filter::class.java)
    }

    /**
     * Filter the original signal
     */
    fun filterSignal(original: ArrayList<Entry>): Float {
        val bs = filter.b.reversed()
        val size = if (bs.size > original.size) original.size else filter.b.size
        val originalSlice = original.takeLast(size)
        return originalSlice.foldIndexed(0f) { index, y, entry -> y + (entry.y * bs[index]) }
//        var y = 0f
//        for (i in originalSlice.indices) {
//            y += originalSlice[i].y * bs[i]
//        }
//        return y
    }
}