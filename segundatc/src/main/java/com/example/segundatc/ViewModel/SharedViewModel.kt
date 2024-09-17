package com.example.segundatc.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    companion object {
        @Volatile private var instance: SharedViewModel? = null
        fun getInstance(): SharedViewModel {
            return instance ?: synchronized(this) {
                instance ?: SharedViewModel().also { instance = it }
            }
        }
    }

    private val _messageSocket = MutableLiveData<String>()
    val messageSocket: LiveData<String> get() = _messageSocket
    fun setMessageSocket(newMessage: String) {
        _messageSocket.postValue(newMessage)
    }

    private val _messageBC = MutableLiveData<String>()
    val messageBC: LiveData<String> get() = _messageBC
    fun setMessageBC(newMessage: String) {
        _messageBC.value = newMessage
    }

    private val _messageIntent = MutableLiveData<String>()
    val messageIntent: LiveData<String> get() = _messageIntent
    fun setMessageIntent(newMessage: String) {
        _messageIntent.value = newMessage
    }

    private val _messageCP = MutableLiveData<String>()
    val messageCP: LiveData<String> get() = _messageCP
    fun setMessageCP(newMessage: String) {
        _messageCP.postValue(newMessage)
    }

    private val _messageBS = MutableLiveData<String>()
    val messageBS: LiveData<String> get() = _messageBS
    fun setMessageBS(newMessage: String) {
        _messageBS.postValue(newMessage)
    }

    private val _messageAIDL = MutableLiveData<String>()
    val messageAIDL: LiveData<String> get() = _messageAIDL
    fun setMessageAIDL(newMessage: String) {
        _messageAIDL.postValue(newMessage)
    }

    private val _messageHearRate = MutableLiveData<String>()
    val messageHearRate: LiveData<String> get() = _messageHearRate
    fun setMessageHearRate(newMessage: String) {
        _messageHearRate.postValue(newMessage)
    }

    private val _messageHandsOn = MutableLiveData<String>()
    val messageHandsOn: LiveData<String> get() = _messageHandsOn
    fun setHandsOn(newMessage: String) {
        _messageHandsOn.postValue(newMessage)
    }

    private val _broadcastResponse = MutableLiveData<String>()
    val broadcastResponse: LiveData<String> get() = _broadcastResponse
    fun setBroadcastResponse(response: String) {
        _broadcastResponse.value = response
    }
}