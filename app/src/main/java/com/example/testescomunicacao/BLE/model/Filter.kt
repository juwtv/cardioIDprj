package com.example.testescomunicacao.BLE.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Filter(@SerializedName("b") @Expose var b: ArrayList<Float> = ArrayList())
