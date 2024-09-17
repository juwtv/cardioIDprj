package com.example.segundatc.androidAuto.popUps

import androidx.annotation.OptIn
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.example.segundatc.androidAuto.auth.AuthenticationFailedScreen
import com.example.segundatc.androidAuto.auth.UserPreferences
import com.example.segundatc.androidAuto.musicPrefs.HighHRVScreen
import com.example.segundatc.androidAuto.musicPrefs.LowHRVScreen
import com.example.shared.SharedConstants.AUTHENTICATION_NOK
import com.example.shared.SharedConstants.BOTH_HANDS_ON_WHEEL
import com.example.shared.SharedConstants.DROWSINESS
import com.example.shared.SharedConstants.HIGH_HRV
import com.example.shared.SharedConstants.LONG_DRIVE
import com.example.shared.SharedConstants.LOW_HRV
import com.example.shared.R
import com.example.shared.SharedConstants.COMMUNICATION

class DemosScreen(carContext: CarContext) : Screen(carContext) {

    private val userPreferences = UserPreferences(carContext)
    private val currentDriver = userPreferences.getCurrentDriver()
    private val driverUuid = currentDriver?.uuid

    @OptIn(ExperimentalCarApi::class)
    override fun onGetTemplate(): Template {
        // Configurando a mensagem e o ícone
        val title = carContext.getString(R.string.demos_title)

        // Criando os itens do grid
        val gridItemList =  if (driverUuid == null) {
            ItemList.Builder()
                .addItem(createGridItem(R.string.demo_drowsiness, R.drawable.ic_drowsiness,DROWSINESS))
                .addItem(createGridItem(R.string.demo_2hands, R.drawable.ic_hands_wheel,BOTH_HANDS_ON_WHEEL))
                .addItem(createGridItem(R.string.demo_break, R.drawable.ic_break,LONG_DRIVE))
                .addItem(createGridItem(R.string.communication, R.drawable.ic_communication,COMMUNICATION))
                .build()
        } else {
            ItemList.Builder()
                .addItem(createGridItem(R.string.demo_drowsiness, R.drawable.ic_drowsiness,DROWSINESS))
                .addItem(createGridItem(R.string.demo_2hands, R.drawable.ic_hands_wheel,BOTH_HANDS_ON_WHEEL))
                .addItem(createGridItem(R.string.demo_break, R.drawable.ic_break,LONG_DRIVE))
                .addItem(createGridItem(R.string.high_hrv_title, R.drawable.ic_relax,HIGH_HRV))
                .addItem(createGridItem(R.string.low_hrv_title, R.drawable.ic_energy,LOW_HRV))
                .addItem(createGridItem(R.string.communication, R.drawable.ic_communication,COMMUNICATION))
                .build()
        }

        return GridTemplate.Builder()
            .setTitle(title)
            .setSingleList(gridItemList)
            .setHeaderAction(Action.BACK)
            .setItemSize(GridTemplate.ITEM_SIZE_LARGE)
            .build()
    }

    private fun createGridItem(titleResId: Int, iconResId: Int, contentId: String): GridItem {
        return GridItem.Builder()
            .setTitle(carContext.getString(titleResId))
            .setImage(
                CarIcon.Builder(
                    IconCompat.createWithResource(carContext, iconResId)
                ).build(), GridItem.IMAGE_TYPE_LARGE
            )
            .setOnClickListener {
                when (contentId) {
                    DROWSINESS -> screenManager.push(DrowsinessPopUpScreen(carContext))
                    BOTH_HANDS_ON_WHEEL -> screenManager.push(BothHandsPopUpScreen(carContext))
                    LONG_DRIVE -> screenManager.push(ProlongedDrivingPopUpScreen(carContext))
                    COMMUNICATION -> screenManager.push(TestesComunicacaoScreen(carContext))
                    HIGH_HRV -> screenManager.push(HighHRVScreen(carContext))
                    LOW_HRV -> screenManager.push(LowHRVScreen(carContext))
                }
            }.build()
    }
}
