package com.example.segundatc.androidAuto.auth

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.example.segundatc.androidAuto.broadcast.BroadcastManager
import com.example.shared.SharedConstants
import com.example.shared.R

class LastSessionScreen(carContext: CarContext, private val driverName: String) : Screen(carContext) {

    override fun onGetTemplate(): Template {

        BroadcastManager.sendScreenActivatedBroadcast(carContext, SharedConstants.TAG_NO_NOTIFICATION_SCREEN)

        val title = carContext.getString(R.string.last_session_title)
        val additionalMessage = "${carContext.getString(R.string.last_session_txt)} $driverName?"
        val yesOption = carContext.getString(R.string.yes_option)
        val noOption = carContext.getString(R.string.no_option)
        val errorIcon = CarIcon.Builder(
            IconCompat.createWithResource(carContext, R.drawable.ic_driver)
        ).build()

        val yesAction = Action.Builder()
            .setTitle(yesOption)
            .setOnClickListener {
                screenManager.push(AuthenticationScreen(carContext, false))
            }
            .build()

        val noAction = Action.Builder()
            .setTitle(noOption)
            .setOnClickListener {
                screenManager.push(ChooseDriverScreen(carContext))

                // Remove a tela atual após o push
                Log.e("FINISH", "LastSessionScreen -> ChooseDriverScreen")
                finish()
            }
            .build()

        return MessageTemplate.Builder(additionalMessage)
            .setIcon(errorIcon)
            .setTitle(title)
            .addAction(yesAction)
            .addAction(noAction)
            .build()
    }
}
