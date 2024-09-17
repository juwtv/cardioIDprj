package com.example.segundatc.androidAuto.auth

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import androidx.lifecycle.Observer
import com.example.segundatc.ViewModel.SharedViewModel
import com.example.segundatc.androidAuto.MainCardioIDScreen
import com.example.segundatc.androidAuto.broadcast.BroadcastManager
import com.example.shared.SharedConstants.AUTHENTICATION_NOK
import com.example.shared.SharedConstants.AUTHENTICATION_OK
import com.example.shared.SharedConstants.TAG_AUTHENTICATION_SCREEN
import com.example.shared.R

class AuthenticationScreen(carContext: CarContext, private val newDriver: Boolean) : Screen(carContext) {

    private val sharedViewModel = SharedViewModel.getInstance()
    override fun onGetTemplate(): Template {

        BroadcastManager.sendScreenActivatedBroadcast(carContext, TAG_AUTHENTICATION_SCREEN)

        val title = if (newDriver) {
            carContext.getString(R.string.new_driver)
        } else {
            carContext.getString(R.string.authenticating_message)
        }
        val additionalMessage = carContext.getString(R.string.additional_auth_message)


        /*Handler(Looper.getMainLooper()).postDelayed({
            screenManager.push(MainCardioIDScreen(carContext))
        }, 5000)*/
        sharedViewModel.broadcastResponse.observe(this, Observer { response ->
            when (response) {
                AUTHENTICATION_OK -> {
                    screenManager.push(MainCardioIDScreen(carContext))
                    sharedViewModel.setBroadcastResponse("")
                    Log.e("setBroadcastResponse", "AuthenticationScreen OK: $response")

                    // Remove a tela atual após o push
                    Log.e("FINISH", "AuthenticationScreen -> MainCardioIDScreen")
                    finish()
                }
                AUTHENTICATION_NOK -> {
                    screenManager.push(AuthenticationFailedScreen(carContext))
                    sharedViewModel.setBroadcastResponse("")
                    Log.e("setBroadcastResponse", "AuthenticationScreen NOK: $response")

                    // Remove a tela atual após o push
                    Log.e("FINISH", "AuthenticationScreen -> AuthenticationFailedScreen")
                    finish()
                }
            }
        })
        /*Handler(Looper.getMainLooper()).postDelayed({
            screenManager.push(MainCardioIDScreen(carContext))
        }, 5000)*/

        return MessageTemplate.Builder(additionalMessage)
            .setHeaderAction(Action.BACK)
            .setTitle(title)
            .setLoading(true)
            .build()
    }
}
