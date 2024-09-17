package com.example.shared

import android.net.Uri

object SharedConstants {

    // --- BroadcastReceiver ---
    const val ACTION_SEND_MESSAGE_BD = "com.example.ACTION_SEND_MESSAGE_BD"
    const val ACTION_SEND_MESSAGE_BD_HEART_RATE = "com.example.ACTION_SEND_MESSAGE_BD_HEART_RATE"

    // --- Intents ---
    const val ACTION_SEND_MESSAGE_INTENT = "com.example.segundatc.RECEIVE_ACTION"

    // --- AIDL ---
    const val ACTION_SEND_MESSAGE_AIDL = "com.example.segundatc.fragments.aidl.AIDLService"

    // --- ContentProvider ---
    // Database
    const val TABLE_NAME = "messages"
    const val COLUMN_ID = "id"
    const val COLUMN_KEY = "key"

    const val AUTHORITY = "com.example.segundatc.fragments.contentProvider"
    val CONTENT_URI = Uri.parse("content://$AUTHORITY/${TABLE_NAME}")



    // --- Notifications // Demo ---
    const val ACTION_SEND_MESSAGE_AUTO = "com.example.segundatc.RECEIVE_ACTION_AUTO" // AppA -> AppB

    const val ACTION_AUTO_SCREEN_ACTIVATED = "com.example.segundatc.SCREEN_ACTIVATED" // AppB -> AppA
    const val SCREEN_NAME = "screen_name"
    const val TAG_AUTHENTICATION_SCREEN = "AuthenticationScreen"
    const val TAG_HOME_SCREEN = "Home"
    const val TAG_ECG_SCREEN = "ECG"
    const val TAG_NO_NOTIFICATION_SCREEN = "None"
    // Authentication
    const val AUTHENTICATION_OK = "Authentication_OK"
    const val AUTHENTICATION_NOK = "Authentication_NOK"
    // Pop-Ups
    const val DROWSINESS = "Drowsiness"
    const val BOTH_HANDS_ON_WHEEL = "BothHandsOnWheel"
    const val LONG_DRIVE = "LongDrive"
    const val HIGH_HRV = "HighHRV"
    const val LOW_HRV = "LowHRV"
    // ECG
    const val ECG_OK = "ECG_OK"
    const val ECG_NOK = "ECG_NOK"

    const val COMMUNICATION = "Communication"

}