package com.example.segundatc.androidAuto.musicPrefs

import android.util.Log
import androidx.annotation.OptIn
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Tab
import androidx.car.app.model.TabContents
import androidx.car.app.model.TabTemplate
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.example.segundatc.androidAuto.auth.UserPreferences
import com.example.shared.R

class MusicPrefScreen(carContext: CarContext) : Screen(carContext) {

    val userPreferences = UserPreferences(carContext)
    val currentDriver = userPreferences.getCurrentDriver()?.uuid

    private val TITLE_RES_IDS = intArrayOf(
        R.string.pref_musicRelax,
        R.string.pref_musicStim
    )

    private val ICON_RES_IDS = intArrayOf(
        R.drawable.ic_relax,
        R.drawable.ic_energy
    )

    private val mTabs: MutableMap<String, Tab> = mutableMapOf()
    private val mTabContentsMap: MutableMap<String, TabContents> = mutableMapOf()
    private var mTabTemplateBuilder: TabTemplate.Builder? = null
    private var mActiveContentId: String? = null

    override fun onGetTemplate(): Template {
        mTabTemplateBuilder = TabTemplate.Builder(object : TabTemplate.TabCallback {
            override fun onTabSelected(tabContentId: String) {
                mActiveContentId = tabContentId
                invalidate()
            }
        }).setHeaderAction(Action.APP_ICON)

        mTabContentsMap.clear()
        mTabs.clear()

        for (i in ICON_RES_IDS.indices) {
            val contentId = i.toString()
            val contentTemplate = when (i) {
                0 -> createMusicRelaxTemplate()
                else -> createMusicStimTemplate()
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

            // Restorar o conteúdo da tab ativa
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

    @OptIn(ExperimentalCarApi::class)
    private fun createMusicRelaxTemplate(): Template {
        // Configurando a mensagem e o ícone
        val title = carContext.getString(R.string.pref_musicRelax)

        // Criando os itens do grid
        val gridItemList = ItemList.Builder()
            .addItem(createMusicRelaxItem(R.string.back, R.drawable.back,"back"))
            .addItem(createMusicRelaxItem(R.string.classic, R.drawable.ic_violin,"classic"))
            .addItem(createMusicRelaxItem(R.string.chill, R.drawable.ic_harp,"chill"))
            .addItem(createMusicRelaxItem(R.string.folk, R.drawable.ic_banjo,"folk"))
            .addItem(createMusicRelaxItem(R.string.jazz, R.drawable.ic_sax,"jazz"))
            .addItem(createMusicRelaxItem(R.string.lofi_hip_hop, R.drawable.ic_headphones,"lofi_hip_hop")) //headphone
            .build()

        return GridTemplate.Builder()
            .setTitle(title)
            .setSingleList(gridItemList)
            .setItemSize(GridTemplate.ITEM_SIZE_LARGE)
            .build()
    }

    private fun createMusicRelaxItem(titleResId: Int, iconResId: Int, contentId: String): GridItem {
        return GridItem.Builder()
            .setTitle(carContext.getString(titleResId))
            .setImage(
                CarIcon.Builder(
                    IconCompat.createWithResource(carContext, iconResId)
                ).build(), GridItem.IMAGE_TYPE_LARGE
            )
            .setOnClickListener {
                when (contentId) {
                    "back" -> {
                        screenManager.pop()
                        Log.e("FINISH", "MusicRelaxPrefScreen -> BACK")
                        finish()
                    }
                    else -> {
                        if (currentDriver != null) {
                            userPreferences.setDriverMusicRelax(currentDriver, carContext.getString(titleResId))
                        CarToast.makeText(carContext, "${carContext.getString(R.string.pref_musicRelax)}: ${carContext.getString(titleResId)}", CarToast.LENGTH_LONG).show()
                            currentDriver.let {userPreferences.getDriverMusicRelax(it)}
                                .let { Log.e("MusicPref", it) }
                        }

                    }
                }
            }
            .build()
    }

    @OptIn(ExperimentalCarApi::class)
    private fun createMusicStimTemplate(): Template {
        // Configurando a mensagem e o ícone
        val title = carContext.getString(R.string.pref_musicStim)

        // Criando os itens do grid
        val gridItemList = ItemList.Builder()
            .addItem(createMusicStimItem(R.string.back, R.drawable.back,"back"))
            .addItem(createMusicStimItem(R.string.eletronic, R.drawable.ic_eletronic,"eletronic"))
            .addItem(createMusicStimItem(R.string.hip_hop, R.drawable.ic_boombox,"hip_hop"))
            .addItem(createMusicStimItem(R.string.metal, R.drawable.ic_metal,"metal"))
            .addItem(createMusicStimItem(R.string.pop, R.drawable.ic_vinyl,"pop"))
            .addItem(createMusicStimItem(R.string.rock, R.drawable.ic_rock,"rock"))
            .build()

        return GridTemplate.Builder()
            .setTitle(title)
            .setSingleList(gridItemList)
            .setItemSize(GridTemplate.ITEM_SIZE_LARGE)
            .build()
    }

    private fun createMusicStimItem(titleResId: Int, iconResId: Int, contentId: String): GridItem {
        return GridItem.Builder()
            .setTitle(carContext.getString(titleResId))
            .setImage(
                CarIcon.Builder(
                    IconCompat.createWithResource(carContext, iconResId)
                ).build(), GridItem.IMAGE_TYPE_LARGE
            )
            .setOnClickListener {
                when (contentId) {
                    "back" -> {
                        screenManager.pop()
                        Log.e("FINISH", "MusicStimPrefScreen -> BACK")
                        finish()
                    }
                    else -> {
                        if (currentDriver != null) {
                            userPreferences.setDriverMusicStim(currentDriver, carContext.getString(titleResId))
                            CarToast.makeText(carContext, "${carContext.getString(R.string.pref_musicStim)}: ${carContext.getString(titleResId)}", CarToast.LENGTH_LONG).show()
                            currentDriver.let {userPreferences.getDriverMusicStim(it)}
                                .let { Log.e("MusicPref", it) }
                        }

                    }
                }
            }
            .build()
    }
}
