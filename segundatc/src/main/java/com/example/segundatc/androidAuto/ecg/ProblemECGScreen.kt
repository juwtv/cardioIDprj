package com.example.segundatc.androidAuto.ecg

import android.util.Log
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

class ProblemECGScreen(carContext: CarContext) : Screen(carContext) {

    init {
        BroadcastManager.sendScreenActivatedBroadcast(carContext, SharedConstants.TAG_NO_NOTIFICATION_SCREEN)
    }

    override fun onGetTemplate(): Template {
        // Configurando a mensagem e o ícone
        val title = carContext.getString(R.string.problem_ecg_title)
        val message = carContext.getString(R.string.problem_ecg)
        val fineOption = carContext.getString(R.string.fine_option)
        val problemOption = carContext.getString(R.string.problem_option)
        val errorIcon = CarIcon.Builder(
            IconCompat.createWithResource(carContext, R.drawable.ic_error)
        ).build()

        val fineAction = Action.Builder()
            .setTitle(fineOption)
            .setOnClickListener {
                screenManager.push(MainCardioIDScreen(carContext))

                Log.e("FINISH", "ProblemECGScreen -> MainCardioIDScreen")
                finish()
            }
            .build()

        val probAction = Action.Builder()
            .setTitle(problemOption)
            .setOnClickListener {
                screenManager.push(EmergencyCallScreen(carContext))
                Log.e("FINISH", "ProblemECGScreen -> EmergencyCallScreen")
                finish()
            }
            .build()

        // Construindo o MessageTemplate
        return MessageTemplate.Builder(message)
            .setIcon(errorIcon)
            .setTitle(title)
            .addAction(fineAction)
            .addAction(probAction)
            .build()
    }
}
