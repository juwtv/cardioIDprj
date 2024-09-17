package com.example.segundatc.androidAuto.popUps

import androidx.annotation.OptIn
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Toggle
import androidx.core.graphics.drawable.IconCompat
import com.example.segundatc.androidAuto.auth.UserPreferences
import com.example.segundatc.androidAuto.ecg.NormalECGScreen
import com.example.shared.R

class PopUpsMainScreen(carContext: CarContext) : Screen(carContext) {

    private val userPreferences = UserPreferences(carContext)
    private val currentDriver = userPreferences.getCurrentDriver()
    private val driverUuid = currentDriver?.uuid


    @OptIn(ExperimentalCarApi::class)
    override fun onGetTemplate(): ListTemplate {
        val POP_UPS = if (driverUuid == null) {
            intArrayOf(
                R.string.pop_up_sleep,
                R.string.pop_up_hands,
                R.string.pop_up_break
            )
        }
        else {
            intArrayOf(
                R.string.pop_up_sleep,
                R.string.pop_up_hands,
                R.string.pop_up_break,
                R.string.low_hrv_title,
                R.string.high_hrv_title
            )
        }

        // Configurando a mensagem e o ícone
        val title = carContext.getString(R.string.def_pop_ups)

        val listBuilder = ItemList.Builder()

        for (i in POP_UPS.indices) {
            listBuilder.addItem(buildRowForTemplate(carContext.getString(POP_UPS[i])))
        }

        return ListTemplate.Builder()
            .setTitle(title)
            .setSingleList(listBuilder.build())
            .setHeaderAction(Action.BACK)
            .addAction(createFABDemo())
            .build()
    }

    private fun buildRowForTemplate(title: CharSequence): Row {
        val rowBuilder = Row.Builder().setTitle(title)

        val toggle = Toggle.Builder { isChecked ->
            CarToast.makeText(carContext, "$title is now ${if (isChecked) "ON" else "OFF"}", CarToast.LENGTH_SHORT).show()
        }.setChecked(true).build()

        rowBuilder.setToggle(toggle)

        return rowBuilder.build()
    }

    private fun createFABDemo(): Action {
        return Action.Builder()
            .setIcon(
                CarIcon.Builder(
                    IconCompat.createWithResource(
                        carContext, R.drawable.ic_demo
                    )
                ).build()
            )
            .setBackgroundColor(CarColor.YELLOW)
            .setOnClickListener {
                screenManager.push(DemosScreen(carContext))
            }
            .build()
    }
}
