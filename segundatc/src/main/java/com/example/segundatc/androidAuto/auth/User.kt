package com.example.segundatc.androidAuto.auth

data class User(
    var uuid: String,
    val name: String,
    var musicRelax: String = "Classic",
    var musicStim: String = "Electronic"
)
