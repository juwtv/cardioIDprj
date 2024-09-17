package com.example.testescomunicacao.fragments.interactionAutoButtons

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.shared.SharedConstants.ACTION_SEND_MESSAGE_AUTO
import com.example.shared.SharedConstants.AUTHENTICATION_NOK
import com.example.shared.SharedConstants.AUTHENTICATION_OK
import com.example.shared.SharedConstants.BOTH_HANDS_ON_WHEEL
import com.example.shared.SharedConstants.DROWSINESS
import com.example.shared.SharedConstants.ECG_NOK
import com.example.shared.SharedConstants.ECG_OK
import com.example.shared.SharedConstants.HIGH_HRV
import com.example.shared.SharedConstants.LONG_DRIVE
import com.example.shared.SharedConstants.LOW_HRV
import com.example.shared.SharedConstants.SCREEN_NAME
import com.example.shared.SharedConstants.TAG_AUTHENTICATION_SCREEN
import com.example.shared.SharedConstants.TAG_ECG_SCREEN
import com.example.shared.SharedConstants.TAG_HOME_SCREEN
import com.example.shared.SharedConstants.TAG_NO_NOTIFICATION_SCREEN
import com.example.testescomunicacao.R
import com.example.testescomunicacao.databinding.FragmentInteractionAutoButtonsBinding
import com.example.testescomunicacao.receiver.ScreenActivatedReceiver
import com.google.android.material.navigation.NavigationView

class InteractionAutoButtonsFragment : Fragment() {

    private var _binding: FragmentInteractionAutoButtonsBinding? = null
    private val binding get() = _binding!!

    private lateinit var drawerMenu: NavigationView
    private lateinit var screenActivatedReceiver: BroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInteractionAutoButtonsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawerMenu = requireActivity().findViewById(R.id.navigationView)

        setupBroadcastReceiver()

        val lastScreenName = ScreenActivatedReceiver.BroadcastStateManager.getLastScreenName(requireContext())
        updateUIBasedOnScreenName(lastScreenName)

        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        // ----- Authentication -----
        binding.btnAcceptDriver.setOnClickListener { sendBroadcastToAutoApp(AUTHENTICATION_OK) }
        binding.btnDenyDriver.setOnClickListener   { sendBroadcastToAutoApp(AUTHENTICATION_NOK) }
        // ----- Pop-Ups -----
        binding.btnDrowsiness.setOnClickListener { sendBroadcastToAutoApp(DROWSINESS) }
        binding.btnBothHands.setOnClickListener  { sendBroadcastToAutoApp(BOTH_HANDS_ON_WHEEL) }
        binding.btnLongDrive.setOnClickListener  { sendBroadcastToAutoApp(LONG_DRIVE) }
        binding.btnHighHRV.setOnClickListener    { sendBroadcastToAutoApp(HIGH_HRV) }
        binding.btnLowHRV.setOnClickListener     { sendBroadcastToAutoApp(LOW_HRV) }
        // ----- ECG -----
        binding.btnEverythingOk.setOnClickListener { sendBroadcastToAutoApp(ECG_OK) }
        binding.btnFoundProblem.setOnClickListener { sendBroadcastToAutoApp(ECG_NOK) }
    }

    private fun sendBroadcastToAutoApp(action: String) {
        Log.d("==> InteractionAutoButtonsFragment", "sendBroadcasts: $action")

        val intent = Intent(ACTION_SEND_MESSAGE_AUTO)
        intent.apply {
            putExtra("response", action)
            setPackage("com.example.segundatc")
        }
        requireContext().sendBroadcast(intent)
    }

    private fun setupBroadcastReceiver() {
        screenActivatedReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    Log.d("==> InteractionAutoButtonsFragment", "onReceive: ${it}")
                    val screenName = it.getStringExtra(SCREEN_NAME)
                    if (screenName != null) {
                        updateUIBasedOnScreenName(screenName)
                    }
                }
            }
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(screenActivatedReceiver,
            IntentFilter("com.example.UPDATE_FRAGMENT"))
    }

    private fun updateUIBasedOnScreenName(screenName: String?) {
        when (screenName) {
            TAG_AUTHENTICATION_SCREEN -> {
                activateSectionAuthentication(true)
                activateSectionPopUps(false)
                activateSectionECG(false)
            }
            TAG_HOME_SCREEN -> {
                activateSectionAuthentication(false)
                activateSectionPopUps(true)
                activateSectionECG(false)
            }
            TAG_ECG_SCREEN -> {
                activateSectionAuthentication(false)
                activateSectionPopUps(false)
                activateSectionECG(true)
            }
            TAG_NO_NOTIFICATION_SCREEN -> {
                activateSectionAuthentication(false)
                activateSectionPopUps(false)
                activateSectionECG(false)
            }
        }
    }

    private fun activateSectionAuthentication(show: Boolean) {
        binding.sectionAuthentication.visibility = if (show) View.VISIBLE else View.GONE
        updateWaitingMessageVisibility()
    }

    private fun activateSectionPopUps(show: Boolean) {
        binding.sectionPopUps.visibility = if (show) View.VISIBLE else View.GONE
        updateWaitingMessageVisibility()
    }

    private fun activateSectionECG(show: Boolean) {
        binding.sectionECG.visibility = if (show) View.VISIBLE else View.GONE
        updateWaitingMessageVisibility()
    }

    private fun updateWaitingMessageVisibility() {
        if (binding.sectionAuthentication.visibility == View.GONE &&
            binding.sectionPopUps.visibility == View.GONE &&
            binding.sectionECG.visibility == View.GONE) {
            binding.tvWaitingNotification.visibility = View.VISIBLE
        } else
            binding.tvWaitingNotification.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(screenActivatedReceiver)
    }
}