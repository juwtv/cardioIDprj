package com.example.segundatc.androidAuto

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class MyLifecycleOwner : LifecycleOwner {
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
    }

    fun doOnResume() {
        //lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        //Log.d("DEBUG AUTO", "LifecycleOwner: ON_RESUME")
    }

    fun doOnPause() {
        //lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        Log.d("DEBUG AUTO", "LifecycleOwner: ON_PAUSE")
    }

    fun doOnDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        Log.d("DEBUG AUTO", "LifecycleOwner: ON_DESTROY")
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
}


