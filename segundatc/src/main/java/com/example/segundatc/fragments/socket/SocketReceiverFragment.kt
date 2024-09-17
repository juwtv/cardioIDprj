package com.example.segundatc.fragments.socket

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.fragment.app.activityViewModels
import com.example.segundatc.Constants.ACTION_MESSAGE_RECEIVED
import com.example.segundatc.R
import com.example.segundatc.ViewModel.SharedViewModel


class SocketReceiverFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var tvMessage: TextView

    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.getStringExtra("message")
            tvMessage.text = message
            sharedViewModel.setMessageSocket(message ?: "")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_socket_receiver, container, false)
        tvMessage = view.findViewById(R.id.tvMessage)

        sharedViewModel.messageSocket.observe(viewLifecycleOwner) { message ->
            tvMessage.text = message
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            messageReceiver, IntentFilter(ACTION_MESSAGE_RECEIVED)
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(messageReceiver)
    }
}