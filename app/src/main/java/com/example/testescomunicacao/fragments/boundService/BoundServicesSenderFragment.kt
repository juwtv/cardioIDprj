package com.example.testescomunicacao.fragments.boundService

import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.shared.IMessageBoundService
import com.example.shared.IMyAidlInterface
import com.example.testescomunicacao.R
import com.example.testescomunicacao.databinding.FragmentBoundServicesSenderBinding

class BoundServicesSenderFragment : Fragment() {

    private var aidlService: IMessageBoundService? = null
    private var _binding: FragmentBoundServicesSenderBinding? = null
    private val binding get() = _binding!!

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            aidlService = IMessageBoundService.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            aidlService = null
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Intent().also { intent ->
            intent.setClassName("com.example.segundatc", "com.example.segundatc.fragments.boundService.BoundService")
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onDetach() {
        super.onDetach()
        activity?.unbindService(serviceConnection)
    }

    private fun sendMessage(message: String) {
        aidlService?.sendMessage(message)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val view = inflater.inflate(R.layout.fragment_bound_services_sender, container, false)

        _binding = FragmentBoundServicesSenderBinding.inflate(inflater, container, false)

        binding.btnSendDataService.setOnClickListener {
            val message = binding.messageToSend.text.toString()
            sendMessage(message)
        }
        return binding.root
    }

}