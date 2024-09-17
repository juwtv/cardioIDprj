package com.example.testescomunicacao


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.shared.SharedConstants.ACTION_AUTO_SCREEN_ACTIVATED
import com.example.testescomunicacao.BLE.Fragment.DeviceFragment
import com.example.testescomunicacao.BLE.Fragment.ScanBleFragment
import com.example.testescomunicacao.BLE.ViewModel.ScanBleViewModel
import com.example.testescomunicacao.BLE.adapter.SelectDevice
import com.example.testescomunicacao.BLE.service.BluetoothLeService
import com.example.testescomunicacao.databinding.ActivityMainBinding
import com.example.testescomunicacao.fragments.aidl.AIDLSenderFragment
import com.example.testescomunicacao.fragments.boundService.BoundServicesSenderFragment
import com.example.testescomunicacao.fragments.broadcastReceiver.BroadcastReceiverSenderFragment
import com.example.testescomunicacao.fragments.contentProvider.ContentProviderSenderFragment
import com.example.testescomunicacao.fragments.intent.IntentsSenderFragment
import com.example.testescomunicacao.fragments.interactionAutoButtons.InteractionAutoButtonsFragment
import com.example.testescomunicacao.fragments.socket.SocketSenderFragment
import com.example.testescomunicacao.receiver.ScreenActivatedReceiver
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), ToolbarTitleChanger {

    private lateinit var mPermissionResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var alertDialog: AlertDialog

    private val permissionsRequest = mutableListOf<String>()
    private val permissions = mutableMapOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE to false,
        Manifest.permission.ACCESS_FINE_LOCATION to false,
        //Manifest.permission.CAMERA to false
    )

    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    private lateinit var binding: ActivityMainBinding

    //private lateinit var viewModel: ScanBleViewModel
    private val viewModel = ScanBleViewModel.getInstance()

    private lateinit var scanBleFragment: ScanBleFragment

    private lateinit var screenActivatedReceiver: ScreenActivatedReceiver

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("--MainActivity", "onCreate")
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions[Manifest.permission.BLUETOOTH_SCAN] = false
            permissions[Manifest.permission.BLUETOOTH_CONNECT] = false
            permissions.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        scanBleFragment = ScanBleFragment()
        mPermissionResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissionsRequested ->
            permissionsRequested.forEach { (permission) -> permissions[permission] = true }
            if(!permissions.values.contains(false)){
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayoutContainer, scanBleFragment)
                    .commit()
            }else{
                finish()
            }
        }
        alertDialog = createDialog()

        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigationView)
        drawerLayout = findViewById(R.id.drawerLayout)

        var toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.cardioID_c3)

        setUpNavigationDrawer()
        navigateToHomeScreen()

        setUpAutoBroadcastReceiver()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun setUpAutoBroadcastReceiver() {
        screenActivatedReceiver = ScreenActivatedReceiver(navigationView)

        val intentFilter = IntentFilter(ACTION_AUTO_SCREEN_ACTIVATED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(screenActivatedReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(screenActivatedReceiver, intentFilter)
        }
    }

    private fun setUpNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_Home -> {
                    setScreen("Home")
                    Log.e("--MainActivity", "menu_Home clicked" +
                            "\n-->device: ${viewModel.devicesAdapter.mSelectedDevice.value}" +
                            "\n-->selectedDevice: ${viewModel.devicesAdapter.getSelectedDevice()}")

                    navigateToHomeScreen()
                    onBackPressed()
                }
                R.id.menu_ContentProviders -> {
                    setScreen("Content Providers")
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.frameLayoutContainer, ContentProviderSenderFragment())
                    fragmentTransaction.commit()
                    onBackPressed()
                }
                R.id.menu_BroadcastReceivers -> {
                    setScreen("Broadcast Receivers")
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.frameLayoutContainer, BroadcastReceiverSenderFragment())
                    fragmentTransaction.commit()
                    onBackPressed()
                }
                R.id.menu_Intents -> {
                    setScreen("Intents")
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.frameLayoutContainer, IntentsSenderFragment())
                    fragmentTransaction.commit()
                    onBackPressed()
                }
                R.id.menu_BoundServices -> {
                    setScreen("Bound Services")
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.frameLayoutContainer, BoundServicesSenderFragment())
                    fragmentTransaction.commit()
                    onBackPressed()
                }
                R.id.menu_AIDL -> {
                    setScreen("AIDL")
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.frameLayoutContainer, AIDLSenderFragment())
                    fragmentTransaction.commit()
                    onBackPressed()
                }
                R.id.menu_Sockets -> {
                    setScreen("Sockets")
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.frameLayoutContainer, SocketSenderFragment())
                    fragmentTransaction.commit()
                    onBackPressed()
                }
                R.id.menu_extra1 -> {
                    setScreen("Accept/Deny/Pop-Ups")
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.frameLayoutContainer, InteractionAutoButtonsFragment())
                    fragmentTransaction.commit()
                    onBackPressed()
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

    private fun navigateToHomeScreen() {
        val device = viewModel.devicesAdapter.getSelectedDevice()
        Log.e("--MainActivity", "navigateToHomeScreen" +
                "\n-->device: ${viewModel.devicesAdapter.mSelectedDevice.value}" +
                "\n-->selectedDevice: ${viewModel.devicesAdapter.getSelectedDevice()}")
        if (device != null) {
            Log.e("--MainActivity", "navigatetoDeviceFragment")
            navigateToDeviceFragment(device)
        } else {
            Log.e("--MainActivity", "navigatetoScanBleFragment")
            navigateToScanBleFragment()
        }
    }

    private fun navigateToDeviceFragment(device: SelectDevice) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayoutContainer)

        if (currentFragment !is DeviceFragment) {
            val fragment = DeviceFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BluetoothLeService.TAG_BLE_DEVICE, device.scanResult)
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutContainer, fragment)
                .commit()
        }
    }

    private fun navigateToScanBleFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayoutContainer)

        if (currentFragment !is ScanBleFragment) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayoutContainer, scanBleFragment)
                .commit()
        }
    }

    fun setScreen(title: String) {
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show()
        setToolbarTitle(title)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()

    }

    override fun setToolbarTitle(title: String) {
        toolbar.title = title
    }

    override fun onStart() {
        super.onStart()
        requestPermissions()
    }
    private fun requestPermissions(){
        permissionsRequest.clear()
        permissions.forEach { (permission) ->
            permissions[permission] = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
            if(!permissions[permission]!!) {
                permissionsRequest.add(permission)
            }
        }
        if(permissionsRequest.isNotEmpty()){
            Log.e("-- permissionsRequest",permissionsRequest.toString())
            alertDialog.show()
        }
    }
    private fun createDialog(): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            builder.setTitle("Permissions")
            builder.setMessage("Your location and access to files to is needed to get the Bluetooth devices nearby you, and save data")
            setPositiveButton("Confirm") { _, _ ->
                mPermissionResultLauncher.launch(permissionsRequest.toTypedArray())
            }
            setNegativeButton("Close") { dialog, _ ->
                dialog.cancel()
                finish()
            }
        }
        return builder.create()
    }

    companion object{
        const val tag = "--MainActivity"
        const val EnrollTime = "EnrollTime"
        const val VerificationTime = "VerificationTime"
        const val defaultEnrollTime = 45
        const val defaultVerificationTime = 30
        const val maxTime = 120
        const val minTime = 10
    }

}