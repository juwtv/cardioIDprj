package com.example.testescomunicacao.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.shared.SharedConstants.SCREEN_NAME
import com.example.shared.SharedConstants.TAG_AUTHENTICATION_SCREEN
import com.example.shared.SharedConstants.TAG_ECG_SCREEN
import com.example.shared.SharedConstants.TAG_HOME_SCREEN
import com.example.shared.SharedConstants.TAG_NO_NOTIFICATION_SCREEN
import com.example.testescomunicacao.R
import com.google.android.material.navigation.NavigationView

/*
 * Classe que atualiza o fragmento de botoes para a DEMO conforme as acoes do utilizador
 * no Android Auto
 */
class ScreenActivatedReceiver(private val drawerMenu: NavigationView) : BroadcastReceiver() {

    object BroadcastStateManager {
        private const val PREFERENCES_NAME = "BroadcastStatePrefs"
        private const val KEY_LAST_SCREEN_NAME = "last_screen_name"

        fun saveLastScreenName(context: Context, screenName: String) {
            val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_LAST_SCREEN_NAME, screenName).apply()
        }

        fun getLastScreenName(context: Context): String? {
            val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            return prefs.getString(KEY_LAST_SCREEN_NAME, TAG_NO_NOTIFICATION_SCREEN)
        }
    }
    override fun onReceive(context: Context, intent: Intent) {
        val screenName = intent.getStringExtra(SCREEN_NAME)

        if (screenName != null) {
            BroadcastStateManager.saveLastScreenName(context, screenName)
            updateDrawerMenuNotification(screenName)

            // Send a local broadcast to update InteractionAutoButtonsFragment
            val localIntent = Intent("com.example.UPDATE_FRAGMENT")
            localIntent.putExtra(SCREEN_NAME, screenName)
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent)
        }
    }

    private fun updateDrawerMenuNotification(screenName: String?) {
        val menuItem = drawerMenu.menu.findItem(R.id.menu_extra1)
        val customView = LayoutInflater.from(drawerMenu.context).inflate(R.layout.drawer_menu_item, null)
        val badge = customView.findViewById<TextView>(R.id.menu_item_badge)
        when (screenName) {
            TAG_AUTHENTICATION_SCREEN -> {
                badge.visibility = View.VISIBLE
                badge.text = "A"
                menuItem.actionView = customView
            }
            TAG_HOME_SCREEN -> {
                badge.visibility = View.VISIBLE
                badge.text = "H"
                menuItem.actionView = customView
            }
            TAG_ECG_SCREEN -> {
                badge.visibility = View.VISIBLE
                badge.text = "E"
                menuItem.actionView = customView
            }
            TAG_NO_NOTIFICATION_SCREEN -> {
                badge.text = ""
                badge.visibility = View.GONE
                menuItem.actionView = customView
            }
        }
    }
}
