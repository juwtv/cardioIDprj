package com.example.segundatc.androidAuto.auth

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import com.example.shared.R

class RemoveDriverScreen(carContext: CarContext) : Screen(carContext) {

    private val userPreferences = UserPreferences(carContext)

    override fun onGetTemplate(): ListTemplate {
        val title = carContext.getString(R.string.remove_driver)
        val drivers = userPreferences.getDrivers()

        val listBuilder = ItemList.Builder()
        drivers.forEach { driver ->
            listBuilder.addItem(
                Row.Builder()
                    .setTitle(driver.name)
                    .setOnClickListener {
                        userPreferences.removeDriver(driver.uuid)
                        screenManager.push(ChooseDriverScreen(carContext))

                        // Remove a tela atual após o push
                        Log.e("FINISH", "RemoveScreen -> ChooseDriverScreen")
                        finish()

                        /*// Retornar um resultado para a tela anterior indicando que um driver foi removido
                        setResult(driver.uuid)
                        finish()*/
                    }
                    .build()
            )
        }

        return ListTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle(title)
            .setHeaderAction(Action.BACK)
            .build()
    }
}
