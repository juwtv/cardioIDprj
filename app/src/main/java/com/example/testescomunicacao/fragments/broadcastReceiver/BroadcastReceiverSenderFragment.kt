package com.example.testescomunicacao.fragments.broadcastReceiver

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shared.SharedConstants.ACTION_SEND_MESSAGE_BD
import com.example.testescomunicacao.databinding.FragmentBroadcastReceiverSenderBinding

class BroadcastReceiverSenderFragment : Fragment() {


    private var _binding: FragmentBroadcastReceiverSenderBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentBroadcastReceiverSenderBinding.inflate(inflater, container, false)

        binding.btnSendBroadcast.setOnClickListener {
            val message = binding.messageToSend.text.toString()
            val intent = Intent().apply {
                action = ACTION_SEND_MESSAGE_BD
                putExtra("message", message)
                setPackage("com.example.segundatc")
            }
            requireContext().sendBroadcast(intent)
        }

        return binding.root
    }
}