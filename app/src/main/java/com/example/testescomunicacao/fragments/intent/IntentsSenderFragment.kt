package com.example.testescomunicacao.fragments.intent

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shared.SharedConstants.ACTION_SEND_MESSAGE_INTENT
import com.example.testescomunicacao.databinding.FragmentIntentsSenderBinding

class IntentsSenderFragment : Fragment() {

    private var _binding: FragmentIntentsSenderBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIntentsSenderBinding.inflate(inflater, container, false)
        binding.btnSendIntent.setOnClickListener{
            sendIntentToReceiverApp()
        }

        return binding.root
    }

    private fun sendIntentToReceiverApp() {
        val textToSend = binding.messageToSend.text.toString()
        val intent = Intent()
        intent.component = ComponentName("com.example.segundatc", "com.example.segundatc.fragments.intent.IntentService")
        intent.action = ACTION_SEND_MESSAGE_INTENT
        intent.putExtra("intent_message", textToSend)
        requireContext().startService(intent)
    }
}