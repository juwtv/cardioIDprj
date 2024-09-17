package com.example.segundatc

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.segundatc.Constants.ACTION_MESSAGE_RECEIVED
import com.example.segundatc.ViewModel.SharedViewModel
import com.example.segundatc.fragments.HomeFragment
import com.example.segundatc.fragments.aidl.AIDLReceiverFragment
import com.example.segundatc.fragments.boundService.BoundServicesReceiverFragment
import com.example.segundatc.fragments.broadcastReceiver.BroadcastReceiverReceiverFragment
import com.example.segundatc.fragments.contentProvider.ContentProviderReceiverFragment
import com.example.segundatc.fragments.intent.IntentsReceiverFragment
import com.example.segundatc.fragments.socket.MessageReceiverService
import com.example.segundatc.fragments.socket.SocketReceiverFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_PHONE_STATE
    )

    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigationView)
        drawerLayout = findViewById(R.id.drawerLayout)

        // Set up the toolbar toggle for navigation drawer
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.cardioID_c3)

        // Set up the navigation drawer
        setUpNavigationDrawer()

        // Handle the intent data if the activity is started with an intent
        handleIntent(intent)

        // --- BroadcastReceiver: socket ---
        // Register broadcast receiver for receiving messages
        LocalBroadcastManager.getInstance(this).registerReceiver(
            messageReceiver, IntentFilter(ACTION_MESSAGE_RECEIVED)
        )
        // --- Socket ---
        // Start the MessageReceiver service
        val serviceIntent = Intent(this, MessageReceiverService::class.java)
        startService(serviceIntent)

        // Inicializa o launcher para solicitações de permissões
        requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allPermissionsGranted = permissions.values.all { it }
            if (allPermissionsGranted) {
                Toast.makeText(this, "Permissões concedidas!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissões negadas!", Toast.LENGTH_SHORT).show()
            }
        }

        // Verifica e solicita permissões se necessário
        if (!hasAllPermissions()) {
            requestPermissionsLauncher.launch(requiredPermissions)
        }
    }

    private fun hasAllPermissions(): Boolean {
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Handle new intents if the activity is already running
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    // Function to handle incoming intents and update the SharedViewModel
    private fun handleIntent(intent: Intent) {
        val receivedData = intent.getStringExtra("intent_message")
        receivedData?.let {
            sharedViewModel.setMessageIntent(it)

        }
    }

    // Local BroadcastReceiver to handle messages from the service
    // Socket
    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.getStringExtra("message")
            message?.let {
                sharedViewModel.setMessageSocket(it)
            }
        }
    }

    private fun setUpNavigationDrawer() {

        // Initial screen
        /*supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutContainer, SocketReceiverFragment())
            .commit()*/
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayoutContainer, HomeFragment())
            .commit()

        // Set up navigation menu items
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_Home -> {
                    setScreen("Home")
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutContainer, HomeFragment())
                        .commit()
                }R.id.menu_ContentProviders -> {
                    setScreen("Content Providers")
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutContainer, ContentProviderReceiverFragment())
                        .commit()
                }
                R.id.menu_BroadcastReceivers -> {
                    setScreen("Broadcast Receivers")
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutContainer, BroadcastReceiverReceiverFragment())
                        .commit()
                }
                R.id.menu_Intents -> {
                    setScreen("Intents")
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutContainer, IntentsReceiverFragment())
                        .commit()
                }
                R.id.menu_BoundServices -> {
                    setScreen("Bound Services")
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutContainer, BoundServicesReceiverFragment())
                        .commit()
                }
                R.id.menu_AIDL -> {
                    setScreen("AIDL")
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutContainer, AIDLReceiverFragment())
                        .commit()
                }
                R.id.menu_Sockets -> {
                    setScreen("Sockets")
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutContainer, SocketReceiverFragment())
                        .commit()
                }
                else -> {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                }
            }
            true
        }
        navigationView.itemIconTintList = null
    }

    fun setScreen(title: String) {
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show()
        toolbar.title = title
        onBackPressed()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
        val intent = Intent(this, MessageReceiverService::class.java)
        stopService(intent)
    }
}