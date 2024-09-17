package com.example.segundatc.androidAuto.popUps

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.example.shared.R

class ProlongedDrivingPopUpScreen(carContext: CarContext) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        // Configurando a mensagem e o ícone
        val title = carContext.getString(R.string.break_title)
        val message = carContext.getString(R.string.break_msg)
        val icon = CarIcon.Builder(
            IconCompat.createWithResource(carContext, R.drawable.ic_break)
        ).build()

        val exitAction = Action.Builder()
            .setIcon(CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.exit)).build())
            .setOnClickListener {
                screenManager.pop()
                Log.e("FINISH", "ProlongedDrivingPopUpScreen -> BACK")
                finish()
            }
            .build()

        // Construindo o MessageTemplate
        return MessageTemplate.Builder(message)
            .setIcon(icon)
            .setTitle(title)
            .addAction(exitAction)
            .build()
    }
}
