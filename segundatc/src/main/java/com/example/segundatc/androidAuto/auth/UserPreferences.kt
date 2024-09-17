package com.example.segundatc.androidAuto.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.UUID

class UserPreferences(context: Context) {
    // Ficheiro JSON
    private val gson = Gson()
    private val file: File = File(context.filesDir, "user_prefs.json")
    // Ficheiro XML
    private val preferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private var users: MutableMap<String, User> = loadUsers()

    private var currentDriverUUID: String? = preferences.getString("currentDriverUUID", null)

    // Método para definir o motorista atual
    fun setCurrentDriverUUID(uuid: String?) {
        Log.d("UserPreferences", "Setting current driver UUID to: $uuid")
        currentDriverUUID = uuid
        preferences.edit().putString("currentDriverUUID", uuid).apply() // Persistindo o UUID
    }

    // Método para recuperar o motorista atual
    fun getCurrentDriver(): User? {
        Log.d("UserPreferences", "Getting current driver with UUID: $currentDriverUUID")
        return currentDriverUUID?.let { getDriver(it) }
    }

    // Carrega os usuários do arquivo JSON
    private fun loadUsers(): MutableMap<String, User> {
        return if (file.exists()) {
            FileReader(file).use { reader ->
                val type = object : TypeToken<MutableMap<String, User>>() {}.type
                gson.fromJson(reader, type) ?: mutableMapOf()
            }
        } else {
            mutableMapOf()
        }
    }

    // Salva os usuários no arquivo JSON
    private fun updateUsersJson() {
        Log.d("UserPreferences", "Saving users: $users")
        FileWriter(file).use { writer ->
            gson.toJson(users, writer)
        }
    }

    // Adiciona um usuário
    fun addDriver(name: String): User {
        val uuid = UUID.randomUUID().toString()
        val user = User(uuid, name)
        users[uuid] = user
        Log.d("UserPreferences", "Adding user: $user")
        updateUsersJson()
        setCurrentDriverUUID(uuid)
        return user
    }

    // Remove um usuário usando UUID
    fun removeDriver(uuid: String) {
        Log.d("UserPreferences", "Removing user with UUID: $uuid")
        users.remove(uuid)
        updateUsersJson()
    }

    // Recupera um usuário específico usando UUID
    fun getDriver(uuid: String): User? {
        return users[uuid]
    }

    // Recupera todos os usuários
    fun getDrivers(): List<User> {
        return users.values.toList()
    }

    // Define a música Relax para um usuário específico
    fun setDriverMusicRelax(uuid: String, musicRelax: String) {
        users[uuid]?.musicRelax = musicRelax
        updateUsersJson()
    }

    // Define a música Stim para um usuário específico
    fun setDriverMusicStim(uuid: String, musicStim: String) {
        users[uuid]?.musicStim = musicStim
        updateUsersJson()
    }

    // Recupera a música Relax para um usuário específico
    fun getDriverMusicRelax(uuid: String): String {
        return users[uuid]?.musicRelax ?: "Classic"
    }

    // Recupera a música Stim para um usuário específico
    fun getDriverMusicStim(uuid: String): String {
        return users[uuid]?.musicStim ?: "Electronic"
    }
}
