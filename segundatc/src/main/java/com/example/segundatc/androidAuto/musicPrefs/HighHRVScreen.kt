package com.example.segundatc.androidAuto.musicPrefs

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.example.segundatc.androidAuto.auth.UserPreferences
import com.example.shared.R

class HighHRVScreen(carContext: CarContext) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        val userPreferences = UserPreferences(carContext)
        val currentDriver = userPreferences.getCurrentDriver()
        val driverUuid = currentDriver?.uuid

        // Configurando a mensagem e o ícone
        val title = carContext.getString(R.string.high_hrv_title)
        val message = "${carContext.getString(R.string.high_hrv_msg)} ${driverUuid?.let { userPreferences.getDriverMusicRelax(it) }} ${carContext.getString(R.string.playlist)}"
        val icon = CarIcon.Builder(
            IconCompat.createWithResource(carContext, R.drawable.ic_relax)
        ).build()

        val playlistAction = Action.Builder()
            .setTitle(carContext.getString(R.string.open_playlist))
            .setIcon(CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.ic_music)).build())
            .build()

        val exitAction = Action.Builder()
            .setIcon(CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.exit)).build())
            .setOnClickListener {
                screenManager.pop()
                Log.e("FINISH", "HighHRVScreen -> BACK")
                finish()
            }
            .build()

        // Construindo o MessageTemplate
        return MessageTemplate.Builder(message)
            .setIcon(icon)
            .setTitle(title)
            .addAction(playlistAction)
            .addAction(exitAction)
            .build()
    }
}
