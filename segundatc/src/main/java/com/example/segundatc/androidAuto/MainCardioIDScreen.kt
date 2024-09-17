package com.example.segundatc.androidAuto

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Action.APP_ICON
import androidx.car.app.model.CarIcon
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Pane
import androidx.car.app.model.PaneTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Tab
import androidx.car.app.model.TabContents
import androidx.car.app.model.TabTemplate
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.Observer
import com.example.segundatc.ViewModel.SharedViewModel
import com.example.segundatc.androidAuto.auth.ChooseDriverScreen
import com.example.segundatc.androidAuto.auth.UserPreferences
import com.example.segundatc.androidAuto.broadcast.BroadcastManager
import com.example.segundatc.androidAuto.ecg.NormalECGScreen
import com.example.segundatc.androidAuto.ecg.ProblemECGScreen
import com.example.segundatc.androidAuto.musicPrefs.MusicPrefScreen
import com.example.segundatc.androidAuto.popUps.PopUpsMainScreen
import com.example.shared.SharedConstants.BOTH_HANDS_ON_WHEEL
import com.example.shared.SharedConstants.DROWSINESS
import com.example.shared.SharedConstants.ECG_NOK
import com.example.shared.SharedConstants.ECG_OK
import com.example.shared.SharedConstants.HIGH_HRV
import com.example.shared.SharedConstants.LONG_DRIVE
import com.example.shared.SharedConstants.LOW_HRV
import com.example.shared.SharedConstants.TAG_ECG_SCREEN
import com.example.shared.SharedConstants.TAG_HOME_SCREEN
import com.example.shared.SharedConstants.TAG_NO_NOTIFICATION_SCREEN
import com.example.shared.R

class MainCardioIDScreen(carContext: CarContext) : Screen(carContext) {

    private val TITLE_RES_IDS = intArrayOf(
        R.string.tab_title_home, R.string.tab_title_bpm,
        R.string.tab_title_ecg, R.string.tab_title_def)

    private val ICON_RES_IDS = intArrayOf(
        R.drawable.ic_home, R.drawable.ic_bpm,
        R.drawable.ic_ecg, R.drawable.ic_definitions)

    private val userPreferences = UserPreferences(carContext)
    private val currentDriver = userPreferences.getCurrentDriver()
    private val driverUuid = currentDriver?.uuid

    private val sharedViewModel = SharedViewModel.getInstance()
    private val lifecycleOwner = MyLifecycleOwner()

    private var isProcessingComplete = false
    private var isECGTabActive = false

    private var messageHearRate: String = ""
    private var messageHandsOn: String = ""

    private var btnDrowsiness: Boolean = false
    private var btnBothHands: Boolean = false
    private var btnLongDrive: Boolean = false
    private var btnLowHRV: Boolean = false
    private var btnHighHRV: Boolean = false

    // Variável para armazenar o estado do pop-up ativo
    private var activePopup: String? = null

    // Handler e Runnable para o temporizador
    private val handler = Handler(Looper.getMainLooper())
    private val returnToHomeRunnable = Runnable {
        activePopup = null
        BroadcastManager.sendScreenActivatedBroadcast(carContext, TAG_HOME_SCREEN)
        invalidate()  // Volta para a tela inicial após 10 segundos
    }

    init {
        lifecycleOwner.doOnResume()
        sharedViewModel.messageHearRate.observe(lifecycleOwner, Observer { newMessage ->
            messageHearRate = newMessage
            if (mActiveContentId == "1") invalidate()
        })
        sharedViewModel.messageHandsOn.observe(lifecycleOwner, Observer { newMessage ->
            messageHandsOn = newMessage
            if (mActiveContentId == "1") invalidate()
        })

        // AppB -> AppA
        BroadcastManager.sendScreenActivatedBroadcast(carContext, TAG_HOME_SCREEN)
        sharedViewModel.setBroadcastResponse("")
        Log.e("setBroadcastResponse", "MainCardioIDScreen init: empty")

        // --- Notifications // Demo ---
        sharedViewModel.broadcastResponse.observe(this, Observer { response ->
            when (response) {
                DROWSINESS -> { activatePopup(DROWSINESS) }
                BOTH_HANDS_ON_WHEEL -> { activatePopup(BOTH_HANDS_ON_WHEEL) }
                LONG_DRIVE -> { activatePopup(LONG_DRIVE) }
                HIGH_HRV -> { activatePopup(HIGH_HRV) }
                LOW_HRV -> { activatePopup(LOW_HRV) }
            }
            invalidate()
        })

        Log.d("MainCardioIDScreen", "ScreenStack:")
        screenManager.screenStack.forEachIndexed { index, screen ->
            Log.d("MainCardioIDScreen","Index: $index, Screen: $screen")
        }
    }

    private val mTabs: MutableMap<String, Tab> = mutableMapOf()
    private val mTabContentsMap: MutableMap<String, TabContents> = mutableMapOf()
    private var mTabTemplateBuilder: TabTemplate.Builder? = null
    private var mActiveContentId: String? = null

    override fun onGetTemplate(): Template {
        lifecycleOwner.doOnResume()

        Log.w("MainCardioIDScreen", "Current driver: $currentDriver")

        // Verifica os booleanos para ativar um pop-up
        when {
            btnDrowsiness -> {
                Log.w("MainCardioIDScreen", "Activating Drowsiness Popup")
                activatePopup(DROWSINESS)
            }
            btnBothHands -> {
                Log.w("MainCardioIDScreen", "Activating BothHands Popup")
                activatePopup(BOTH_HANDS_ON_WHEEL)
            }
            btnLongDrive -> {
                Log.w("MainCardioIDScreen", "Activating LongDrive Popup")
                activatePopup(LONG_DRIVE)
            }
            btnLowHRV -> {
                Log.w("MainCardioIDScreen", "Activating LowHRV Popup")
                activatePopup(LOW_HRV)
            }
            btnHighHRV -> {
                Log.w("MainCardioIDScreen", "Activating HighHRV Popup")
                activatePopup(HIGH_HRV)
            }
        }

        // Verifica se há um pop-up ativo
        activePopup?.let {
            return when (it) {
                DROWSINESS -> createDrowsinessTemplate()
                BOTH_HANDS_ON_WHEEL -> createBothHandsTemplate()
                LONG_DRIVE -> createLongDriveTemplate()
                LOW_HRV -> createLowHRVTemplate()
                HIGH_HRV -> createHighHRVTemplate()
                else -> createHomeTemplate()
            }
        }

        mTabTemplateBuilder = TabTemplate.Builder(object : TabTemplate.TabCallback {
            override fun onTabSelected(tabContentId: String) {
                mActiveContentId = tabContentId

                // Verifique se a aba selecionada é a "ECG"
                when (tabContentId) {
                    "0" -> {
                        BroadcastManager.sendScreenActivatedBroadcast(carContext, TAG_HOME_SCREEN)
                        sharedViewModel.setBroadcastResponse("")
                        Log.e("setBroadcastResponse", "MainCardioIDScreen onGetTemplate \"0\": empty")
                        isECGTabActive = false
                    }
                    "1" -> {
                        BroadcastManager.sendScreenActivatedBroadcast(carContext, TAG_NO_NOTIFICATION_SCREEN)
                        sharedViewModel.setBroadcastResponse("")
                        Log.e("setBroadcastResponse", "MainCardioIDScreen onGetTemplate \"1\": empty")
                        isECGTabActive = false
                    }
                    "2" -> {
                        BroadcastManager.sendScreenActivatedBroadcast(carContext, TAG_ECG_SCREEN)
                        sharedViewModel.setBroadcastResponse("")
                        Log.e("setBroadcastResponse", "MainCardioIDScreen onGetTemplate \"2\": empty")
                        isECGTabActive = true
                        isProcessingComplete = false
                        startECGProcessing()
                    }
                    "3" -> {
                        BroadcastManager.sendScreenActivatedBroadcast(carContext, TAG_NO_NOTIFICATION_SCREEN)
                        sharedViewModel.setBroadcastResponse("")
                        Log.e("setBroadcastResponse", "MainCardioIDScreen onGetTemplate \"3\": empty")
                        isECGTabActive = false
                    }
                }
                invalidate()
            }
        }).setHeaderAction(APP_ICON)

        mTabContentsMap.clear()
        mTabs.clear()

        for (i in ICON_RES_IDS.indices) {
            val contentId = i.toString()
            val contentTemplate = when (i) {
                0 -> createHomeTemplate()
                1 -> createBPMTemplate()
                2 -> createECGTemplate()
                else -> if (driverUuid == null) createGuestDefinitionsTemplate() else createDefinitionsTemplate()
            }

            val tabContents = TabContents.Builder(contentTemplate).build()
            mTabContentsMap[contentId] = tabContents

            val tabBuilder = Tab.Builder()
                .setTitle(carContext.getString(TITLE_RES_IDS[i]))
                .setIcon(
                    CarIcon.Builder(
                        IconCompat.createWithResource(
                            carContext, ICON_RES_IDS[i]
                        )
                    ).build()
                )
                .setContentId(contentId)

            if (mActiveContentId.isNullOrEmpty() && i == 0) {
                mActiveContentId = contentId
                mTabTemplateBuilder!!.setTabContents(tabContents)
            } else if (mActiveContentId == contentId) {
                mTabTemplateBuilder!!.setTabContents(tabContents)
            }

            val tab = tabBuilder.build()
            mTabs[tab.contentId] = tab
            mTabTemplateBuilder!!.addTab(tab)
        }

        return mActiveContentId?.let { mTabTemplateBuilder!!.setActiveTabContentId(it).build() }!!
    }

    private fun createHomeTemplate(): MessageTemplate {
        val driverNameDisplay = if (driverUuid == null) {
            "${carContext.getString(R.string.hello)} ${carContext.getString(R.string.driver)}"
        } else {
            if (currentDriver != null) {"${carContext.getString(R.string.hello)} ${currentDriver.name}"}
            else { carContext.getString(R.string.hello) }
        }

        return MessageTemplate.Builder(driverNameDisplay)
            .setTitle(driverNameDisplay)
            .setIcon(CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.ic_cardioid)).build())
            .build()
    }

    // Função para ativar um pop-up
    private fun activatePopup(popupType: String) {
        activePopup = popupType
        invalidate()
        handler.postDelayed(returnToHomeRunnable, 10000)  // Volta ao Home em 10 segundos

    }

    /* Templates dos Pop-Ups */
    private fun createDrowsinessTemplate(): MessageTemplate {
        BroadcastManager.sendScreenActivatedBroadcast(carContext, TAG_NO_NOTIFICATION_SCREEN)
        sharedViewModel.setBroadcastResponse("")
        Log.e("setBroadcastResponse", "MainCardioIDScreen createDrowsinessTemplate: empty")

        val title = carContext.getString(R.string.drowsiness_title)
        val message = carContext.getString(R.string.drowsiness_msg)
        val icon = CarIcon.Builder(
            IconCompat.createWithResource(carContext, R.drawable.ic_coffee)
        ).build()

        val exitAction = Action.Builder()
            .setIcon(CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.exit)).build())
            .setOnClickListener {
                BroadcastManager.sendScreenActivatedBroadcast(carContext, TAG_HOME_SCREEN)
                sharedViewModel.setBroadcastResponse("")
                Log.e("setBroadcastResponse", "MainCardioIDScreen createDrowsinessTemplate: empty")
                activePopup = null
                handler.removeCallbacks(returnToHomeRunnable)  // Cancela o timer
                invalidate()  // Volta para a tela inicial
            }
            .build()

        return MessageTemplate.Builder(message)
            .setIcon(icon)
            .setTitle(title)
            .addAction(exitAction)
            .build()
    }

    private fun createBothHandsTemplate(): MessageTemplate {
        BroadcastManager.sendScreenActivatedBroadcast(carContext, TAG_NO_NOTIFICATION_SCREEN)
        sharedViewModel.setBroadcastResponse("")
        Log.e("setBroadcastResponse", "MainCardioIDScreen createBothHandsTemplate: empty")

        val message = carContext.getString(R.string.two_hands_msg)
        val icon = CarIcon.Builder(
            IconCompat.createWithResource(carContext, R.drawable.ic_hands_wheel)
        ).build()

        val exitAction = Action.Builder()
            .setIcon(CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.exit)).build())
            .setOnClickListener {
                BroadcastManager.sendScreenActivatedBroadcast(carContext, TAG_HOME_SCREEN)
                sharedViewModel.setBroadcastResponse("")
                Log.e("setBroadcastResponse", "MainCardioIDScreen createBothHandsTemplate 2: empty")
                activePopup = null
                handler.removeCallbacks(returnToHomeRunnable)  // Cancela o timer
                invalidate()  // Volta para a tela inicial
            }
            .build()

        return MessageTemplate.Builder(message)
            .setIcon(icon)
            .addAction(exitAction)
            .build()
    }

    private fun createLongDriveTemplate(): MessageTemplate {
        BroadcastManager.sendScreenActivatedBroadcast(carContext, TAG_NO_NOTIFICATION_SCREEN)
        sharedViewModel.setBroadcastResponse("")
        Log.e("setBroadcastResponse", "MainCardioIDScreen createLongDriveTemplate: empty")

        val title = carContext.getString(R.string.break_title)
        val message = carContext.getString(R.string.break_msg)
        val icon = CarIcon.Builder(
            IconCompat.createWithResource(carContext, R.drawable.ic_break)
        ).build()

        val exitAction = Action.Builder()
            .setIcon(CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.exit)).build())
            .setOnClickListener {
                BroadcastManager.sendScreenActivatedBroadcast(carContext, TAG_HOME_SCREEN)
                sharedViewModel.setBroadcastResponse("")
                Log.e("setBroadcastResponse", "MainCardioIDScreen createLongDriveTemplate 2: empty")
                activePopup = null
                handler.removeCallbacks(returnToHomeRunnable)  // Cancela o timer
                invalidate()  // Volta para a tela inicial
            }
            .build()

        return MessageTemplate.Builder(message)
            .setIcon(icon)
            .setTitle(title)
            .addAction(exitAction)
            .build()
    }

    private fun createLowHRVTemplate(): MessageTemplate {
        BroadcastManager.sendScreenActivatedBroadcast(carContext, TAG_NO_NOTIFICATION_SCREEN)
        sharedViewModel.setBroadcastResponse("")
        Log.e("setBroadcastResponse", "MainCardioIDScreen creteLowHRVTemplate: empty")

        val title = carContext.getString(R.string.low_hrv_title)
        val message = "${carContext.getString(R.string.low_hrv_msg)} " +
                "${driverUuid?.let { userPreferences.getDriverMusicStim(it) }} ${carContext.getString(R.string.playlist)}"
        val icon = CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.ic_energy)).build()

        val playlistAction = Action.Builder()
            .setTitle(carContext.getString(R.string.open_playlist))
            .setIcon(CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.ic_music)).build())
            .build()

        val exitAction = Action.Builder()
            .setIcon(CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.exit)).build())
            .setOnClickListener {
                BroadcastManager.sendScreenActivatedBroadcast(carContext, TAG_HOME_SCREEN)
                sharedViewModel.setBroadcastResponse("")
                Log.e("setBroadcastResponse", "MainCardioIDScreen creteLowHRVTemplate 2: empty")
                activePopup = null
                handler.removeCallbacks(returnToHomeRunnable)
                invalidate()
            }
            .build()

        return MessageTemplate.Builder(message)
            .setIcon(icon)
            .setTitle(title)
            .addAction(playlistAction)
            .addAction(exitAction)
            .build()
    }

    private fun createHighHRVTemplate(): MessageTemplate {
        BroadcastManager.sendScreenActivatedBroadcast(carContext, TAG_NO_NOTIFICATION_SCREEN)
        sharedViewModel.setBroadcastResponse("")
        Log.e("setBroadcastResponse", "MainCardioIDScreen createHighHRVTemplate: empty")

        val title = carContext.getString(R.string.high_hrv_title)
        val message = "${carContext.getString(R.string.high_hrv_msg)} " +
                "${driverUuid?.let { userPreferences.getDriverMusicRelax(it) }} ${carContext.getString(R.string.playlist)}"
        val icon = CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.ic_relax)).build()

        val playlistAction = Action.Builder()
            .setTitle(carContext.getString(R.string.open_playlist))
            .setIcon(CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.ic_music)).build())
            .build()

        val exitAction = Action.Builder()
            .setIcon(CarIcon.Builder(IconCompat.createWithResource(carContext, R.drawable.exit)).build())
            .setOnClickListener {
                BroadcastManager.sendScreenActivatedBroadcast(carContext, TAG_HOME_SCREEN)
                sharedViewModel.setBroadcastResponse("")
                Log.e("setBroadcastResponse", "MainCardioIDScreen createHighHRVTemplate 2: empty")
                activePopup = null
                handler.removeCallbacks(returnToHomeRunnable)
                invalidate()
            }
            .build()

        return MessageTemplate.Builder(message)
            .setIcon(icon)
            .setTitle(title)
            .addAction(playlistAction)
            .addAction(exitAction)
            .build()
    }

    private fun createBPMTemplate(): PaneTemplate {

        // imagem
        val paneBuilder = Pane.Builder().setImage(
            CarIcon.Builder(
                when (messageHandsOn) {
                    "0" -> IconCompat.createWithResource(carContext, R.drawable.hands_on_none_square)
                    "1" -> IconCompat.createWithResource(carContext, R.drawable.hands_on_both_square)
                    else -> IconCompat.createWithResource(carContext, R.drawable.hands_on_none_square)
                }
            ).build()
        )

        val msg = Row.Builder()
            .setTitle(
                when (messageHandsOn) {
                    "1", "2", "3" -> carContext.getString(R.string.monitoring)
                    else -> carContext.getString(R.string.place_hands)
                }
            )
            .build()

        val noBPM = "--- BPM"
        val BPM = "$messageHearRate BPM"
        val heartRateRow = Row.Builder()
            .setTitle(
                if (messageHearRate == "") {
                    noBPM
                } else {
                    when (messageHandsOn) {
                        "1", "2", "3" -> BPM
                        else -> noBPM
                    }
                })
            .setImage(CarIcon.Builder(
                IconCompat.createWithResource(carContext, R.drawable.ic_bpm)).build())
            .build()

        return PaneTemplate.Builder(paneBuilder
            .addRow(msg)
            .addRow(heartRateRow).build())
            .build()
    }

    private fun createECGTemplate(): MessageTemplate {
        // Mensagem de carregamento inicial
        val loadingMessage = carContext.getString(R.string.loading_ecg)

        // Retornar o template de carregamento
        return MessageTemplate.Builder(loadingMessage)
            .setTitle(loadingMessage)
            .setLoading(true)
            .build()
    }

    private fun startECGProcessing() {
        if (isECGTabActive && !isProcessingComplete) {
            isProcessingComplete = true  // Marca o processamento como completo para evitar múltiplas execuções

            sharedViewModel.broadcastResponse.observe(this, Observer { response ->
                when (response) {
                    ECG_OK -> {
                        screenManager.push(NormalECGScreen(carContext))

                        Log.e("FINISH", "MainCardioIDScreen -> NormalECGScreen")
                        finish()
                    }
                    ECG_NOK -> {
                        screenManager.push(ProblemECGScreen(carContext))

                        Log.e("FINISH", "MainCardioIDScreen -> ProblemECGScreen")
                        finish()
                    }
                }
            })
        }
    }

    private fun createGuestDefinitionsTemplate(): ListTemplate {
        // Criação dos itens da lista
        val itemList = ItemList.Builder()
            .addItem(
                Row.Builder()
                    .setImage(
                        CarIcon.Builder(
                            IconCompat.createWithResource(carContext, R.drawable.ic_pop_up)
                        ).build()
                    )
                    .setTitle(carContext.getString(R.string.def_pop_ups))
                    .setBrowsable(true)
                    .setOnClickListener {
                        screenManager.push(PopUpsMainScreen(carContext))
                    }.build()
            )
            .addItem(
                Row.Builder()
                    .setImage(
                        CarIcon.Builder(
                            IconCompat.createWithResource(carContext, R.drawable.ic_logout)
                        ).build()
                    )
                    .setTitle(carContext.getString(R.string.create_driver))
                    .setBrowsable(true)
                    .setOnClickListener {
                        screenManager.push(ChooseDriverScreen(carContext))
                        Log.e("FINISH", "MainCardioIDScreen -> ChooseDriverScreen")
                        finish()
                    }.build()
            ).build()


        // Criação do ListTemplate
        return ListTemplate.Builder()
            .setSingleList(itemList)
            .build()
    }

    private fun createDefinitionsTemplate(): ListTemplate {
        // Criação dos itens da lista
        val itemList = ItemList.Builder()
            .addItem(
                Row.Builder()
                    .setImage(
                        CarIcon.Builder(
                            IconCompat.createWithResource(carContext, R.drawable.ic_pop_up)
                        ).build()
                    )
                    .setTitle(carContext.getString(R.string.def_pop_ups))
                    .setBrowsable(true)
                    .setOnClickListener {
                        screenManager.push(PopUpsMainScreen(carContext))
                    }.build()
            )
            .addItem(
                Row.Builder()
                    .setImage(
                        CarIcon.Builder(
                            IconCompat.createWithResource(carContext, R.drawable.ic_music)
                        ).build()
                    )
                    .setTitle(carContext.getString(R.string.music_prefs))
                    .setBrowsable(true)
                    .setOnClickListener {
                        screenManager.push(MusicPrefScreen(carContext))
                    }.build()
            )
            .addItem(
                Row.Builder()
                    .setImage(
                        CarIcon.Builder(
                            IconCompat.createWithResource(carContext, R.drawable.ic_logout)
                        ).build()
                    )
                    .setTitle(carContext.getString(R.string.change_driver))
                    .setBrowsable(true)
                    .setOnClickListener {
                        userPreferences.setCurrentDriverUUID(null)
                        Log.d("setUUID_Sair", "Current driver UUID: ${userPreferences.getCurrentDriver()}")
                        screenManager.push(ChooseDriverScreen(carContext))

                        Log.e("FINISH", "MainCardioIDScreen -> ChooseDriverScreen")
                        finish()
                    }.build()
            ).build()


        // Criação do ListTemplate
        return ListTemplate.Builder()
            .setSingleList(itemList)
            .build()
    }
}