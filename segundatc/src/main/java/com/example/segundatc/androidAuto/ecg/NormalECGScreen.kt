package com.example.segundatc.androidAuto.ecg

import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.example.segundatc.androidAuto.MainCardioIDScreen
import com.example.segundatc.androidAuto.broadcast.BroadcastManager
import com.example.shared.R
import com.example.shared.SharedConstants

class NormalECGScreen(carContext: CarContext) : Screen(carContext) {

    init {
        BroadcastManager.sendScreenActivatedBroadcast(carContext, SharedConstants.TAG_NO_NOTIFICATION_SCREEN)
    }

    override fun onGetTemplate(): Template {
        // Configurando a mensagem e o ícone
        val title = carContext.getString(R.string.normal_ecg_title)
        val message = carContext.getString(R.string.normal_ecg)
        val thumbsUpIcon = CarIcon.Builder(
            IconCompat.createWithResource(carContext, R.drawable.ic_thumbs_up)
        ).build()

        val exitAction = Action.Builder()
            .setIcon(CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.exit)).build())
            .setOnClickListener {
                screenManager.push(MainCardioIDScreen(carContext))
                Log.e("FINISH", "NormalECGScreen -> MainCardioIDScreen")
                finish()
            }
            .build()

        // Construindo o MessageTemplate
        return MessageTemplate.Builder(message)
            .setIcon(thumbsUpIcon)
            .setTitle(title)
            .addAction(exitAction)
            .build()
    }
}
