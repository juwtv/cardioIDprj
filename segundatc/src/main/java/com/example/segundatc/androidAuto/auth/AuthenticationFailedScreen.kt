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

class AuthenticationFailedScreen(carContext: CarContext) : Screen(carContext) {

    override fun onGetTemplate(): Template {

        BroadcastManager.sendScreenActivatedBroadcast(carContext, SharedConstants.TAG_NO_NOTIFICATION_SCREEN)

        val title = carContext.getString(R.string.auth_failed)
        val retryTitle = carContext.getString(R.string.retry)
        val otherDriver = carContext.getString(R.string.otherDriver)
        val additionalMessage = carContext.getString(R.string.additional_auth_failed)
        val errorIcon = CarIcon.Builder(
            IconCompat.createWithResource(carContext, R.drawable.ic_error)
        ).build()

        val retryAction = Action.Builder()
            .setTitle(retryTitle)
            .setOnClickListener {
                screenManager.push(AuthenticationScreen(carContext, false))
                Log.e("FINISH", "AuthenticationFailedScreen -> AuthenticationScreen")
                finish()
            }
            .build()

        val otherDriverAction = Action.Builder()
            .setTitle(otherDriver)
            .setOnClickListener {
                screenManager.push(ChooseDriverScreen(carContext))
                Log.e("FINISH", "AuthenticationFailedScreen -> ChooseDriverScreen")
                finish()
            }
            .build()

        return MessageTemplate.Builder(additionalMessage)
            .setIcon(errorIcon)
            .setTitle(title)
            .addAction(retryAction)
            .addAction(otherDriverAction)
            .build()
    }
}
