package com.example.segundatc.androidAuto.auth

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.OptIn
import androidx.car.app.CarContext
import androidx.car.app.OnScreenResultListener
import androidx.car.app.Screen
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.OnClickListener
import androidx.core.graphics.drawable.IconCompat
import com.example.segundatc.androidAuto.MainCardioIDScreen
import com.example.shared.R

class ChooseDriverScreen(carContext: CarContext) : Screen(carContext) {

    init {
        Log.d("ChooseDriverScreen", "ScreenStack:")
        screenManager.screenStack.forEachIndexed { index, screen ->
            Log.d("ChooseDriverScreenpop","Index: $index, Screen: $screen")
        }
    }

    private val userPreferences = UserPreferences(carContext)

    @OptIn(ExperimentalCarApi::class)
    override fun onGetTemplate(): GridTemplate {

        //BroadcastManager.sendScreenActivatedBroadcast(carContext, CHOOSE_DRIVER_SCREEN)
        //BroadcastManager.sendScreenActivatedBroadcast(carContext, "ChooseDriverScreen")
        val drivers = userPreferences.getDrivers()
        val listSize = drivers.size + 3
        val title = carContext.getString(R.string.choose_driver)

        val listBuilder = ItemList.Builder()
        for (i in 0 until listSize) {
            when (i) {
                listSize - 3 -> {
                    listBuilder.addItem(buildGridItemForTemplate(
                        carContext.getString(R.string.guest),
                        R.drawable.ic_driver) {
                        userPreferences.setCurrentDriverUUID(null)
                        Log.d("setUUID", "Current driver UUID: ${userPreferences.getCurrentDriver()}")
                        screenManager.push(MainCardioIDScreen(carContext))

                        Log.e("FINISH", "ChooseDriverScreen -> MainCardioIDScreen")
                        finish()
                    })
                }

                listSize - 2 -> {
                    listBuilder.addItem(buildGridItemForTemplate(
                        carContext.getString(R.string.add_driver),
                        R.drawable.ic_add_driver) {
                        screenManager.push(CreateDriverScreen(carContext))
                    })
                }
                listSize - 1 -> {
                    listBuilder.addItem(buildGridItemForTemplate(
                        carContext.getString(R.string.remove_driver),
                        R.drawable.ic_remove_driver) {
                        screenManager.push(RemoveDriverScreen(carContext))

                        /*screenManager.pushForResult(
                            RemoveDriverScreen(carContext)
                        ) { result ->
                            if (result is String) {
                                Log.d("ChooseDriverScreen", "Driver removed: $result")
                                // Atualize a tela para refletir a remoção do driver
                                invalidate() // Recria o template chamando onGetTemplate
                            }
                        }*/
                    })
                }
                else -> {
                    val driver = drivers[i]
                    listBuilder.addItem(buildGridItemForTemplate(
                        driver.name,
                        R.drawable.ic_driver) {
                        userPreferences.setCurrentDriverUUID(driver.uuid)
                        Log.d("setUUID", "Current driver UUID: ${userPreferences.getCurrentDriver()}")
                        screenManager.push(AuthenticationScreen(carContext, false))
                    })
                    Log.d("ChooseDriverScreen", "Current driver: ${driver.name}")
                }
            }
        }

        // Configurando as ações na barra inferior
        return GridTemplate.Builder()
            .setSingleList(listBuilder.build())
            .setTitle(title)
            .build()
    }

    private fun buildGridItemForTemplate(title: CharSequence,  @DrawableRes resId: Int,  onClick: OnClickListener): GridItem {
        return GridItem.Builder()
             .setImage(
                CarIcon.Builder(IconCompat.createWithResource(carContext, resId)).build(),
                GridItem.IMAGE_TYPE_LARGE
            )
            .setTitle(title)
            .setOnClickListener(onClick)
            .build()
    }
}