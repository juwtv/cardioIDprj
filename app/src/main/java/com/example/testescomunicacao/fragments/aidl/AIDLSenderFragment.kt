package com.example.testescomunicacao.fragments.aidl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shared.SharedConstants.ACTION_SEND_MESSAGE_AIDL
import com.example.shared.IMyAidlInterface
import com.example.testescomunicacao.databinding.FragmentAidlSenderBinding

class AIDLSenderFragment : Fragment() {

    private var _binding: FragmentAidlSenderBinding? = null
    private val binding get() = _binding!!
    private var aidlService: IMyAidlInterface? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            aidlService = IMyAidlInterface.Stub.asInterface(service)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            aidlService = null
            isBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.e("AIDLSender", "onCreateView")
        _binding = FragmentAidlSenderBinding.inflate(inflater, container, false)

        binding.btnSendDataAidl.setOnClickListener {
            val message = binding.messageToSend.text.toString()
            if (isBound && aidlService != null) {
                try {
                    aidlService?.sendMessage(message)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            } else {
                Log.d("AIDLSender", "Service is not bound")
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent().apply {
            component = ComponentName("com.example.segundatc", ACTION_SEND_MESSAGE_AIDL)
        }

        //activity?.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        if (requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE))
            Log.d("AIDLSender", "=> service binded")
        else Log.d("AIDLSender", "=> service not binded")
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            activity?.unbindService(serviceConnection)
            isBound = false
        }
    }
}