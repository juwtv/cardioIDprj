package com.example.testescomunicacao.fragments.socket

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.testescomunicacao.R
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import android.net.wifi.WifiManager
import android.text.format.Formatter
import android.widget.TextView
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.testescomunicacao.ToolbarTitleChanger
import com.example.testescomunicacao.databinding.FragmentSocketSenderBinding

class SocketSenderFragment : Fragment() {

    private var _binding: FragmentSocketSenderBinding? = null
    private val binding get() = _binding!!
    private var toolbarTitleChanger: ToolbarTitleChanger? = null
    private var isServiceRunning = false

    private val serviceMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.getStringExtra("message")?.let { message ->
                binding.textInfoBackground.text = message
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ToolbarTitleChanger) {
            toolbarTitleChanger = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        toolbarTitleChanger = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // TODO: remover o view daqui, ficar só com o binding
        //  ----- (ja tirei os lateinit de buttons, editText, viewText, etc) -----

        //val view = inflater.inflate(R.layout.fragment_socket_sender, container, false)
        _binding = FragmentSocketSenderBinding.inflate(inflater, container, false)
        val view = binding.root

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.socket_sender)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.ipSocketToSend.setText("192.168.1.104")

        val wifiManager = requireActivity().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress: String = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        //Log.e("DEBUG: IP", ipAddress)
        //binding.ipView.text = ipAddress
        binding.ipView.text = "Sending to localhost: 127.0.0.1"

        toolbarTitleChanger?.setToolbarTitle("Sockets: $ipAddress")

        //val myThread = Thread(MyServerThread(requireActivity().applicationContext))
        //myThread.start()

        binding.buttonSocketSender.setOnClickListener {
            send(view)
        }

        // ----- BACKGROUND -----
        binding.textInfoBackground.text = "SERVICE: NOT TURNED ON"

        binding.btnStartBackground.setOnClickListener {
            Log.e("debug: SocketSenderFragment", "Clicked start background")
            val ip = binding.ipSocketToSend.text.toString()
            startSocketService(ip)
            isServiceRunning = true
            updateButtonColors()
            binding.textInfoBackground.text = "SERVICE STARTED"
        }

        binding.btnStopBackground.setOnClickListener {
            Log.e("debug: SocketSenderFragment", "Clicked stop background")
            stopSocketService()
            isServiceRunning = false
            updateButtonColors()
            binding.textInfoBackground.text = "SERVICE STOPPED"
        }

        binding.btnSendBackground.setOnClickListener {
            Log.e("debug: SocketSenderFragment", "Clicked send background")
            val message = binding.messageToSend.text.toString()
            sendMessage(message)
            binding.textInfoBackground.text = "Background sent: $message"
        }

        updateButtonColors()

        return view
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            serviceMessageReceiver, IntentFilter("com.example.testescomunicacao.SERVICE_MESSAGE")
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(serviceMessageReceiver)
    }

    private fun updateButtonColors() {
        if (isServiceRunning) {
            binding.btnStartBackground.setBackgroundColor(Color.GREEN)
            binding.btnStopBackground.setBackgroundColor(Color.RED)
            binding.btnSendBackground.setBackgroundColor(Color.BLUE)
        } else {
            binding.btnStartBackground.setBackgroundColor(resources.getColor(R.color.cardioID_c1))
            binding.btnStopBackground.setBackgroundColor(resources.getColor(R.color.cardioID_c1))
            binding.btnSendBackground.setBackgroundColor(resources.getColor(R.color.cardioID_c1))
        }
    }

    private fun startSocketService(ip: String) {
        activity?.let {
            val intent = Intent(it, SocketService::class.java).apply {
                action = SocketService.ACTION_START
                putExtra(SocketService.EXTRA_IP, ip)
            }
            it.startService(intent)
        }
    }

    private fun stopSocketService() {
        activity?.let {
            val intent = Intent(it, SocketService::class.java).apply {
                action = SocketService.ACTION_STOP
            }
            it.startService(intent)
        }
    }

    private fun sendMessage(message: String) {
        activity?.let {
            val intent = Intent(it, SocketService::class.java).apply {
                action = SocketService.ACTION_SEND
                putExtra(SocketService.EXTRA_MESSAGE, message)
            }
            it.startService(intent)
        }
    }


    private fun send(view: View) {
        Log.d("-- Debug: SocketSenderFragment --", "entered send")
        val messageSender = MessageSender(view)
        messageSender.execute(binding.messageToSend.text.toString())
    }

    class MyServerThread(private var context: Context) : Runnable {

        private lateinit var s: Socket
        private lateinit var ss: ServerSocket
        private lateinit var isr: InputStreamReader
        private lateinit var br: BufferedReader
        private lateinit var message: String

        private var h: Handler = Handler()

        override fun run() {
            try {
                ss = ServerSocket(7801) // Listen
                Log.d("-- debug: MyServerThread --", "Server started on port 7801")

                while (true) {
                    s = ss.accept() // Wait
                    isr = InputStreamReader(s.getInputStream())
                    br = BufferedReader(isr)
                    message  = br.readLine()
                    h.post {
                        //Log.d("-- Debug: SocketSenderFragment --", "RECEIVED FROM JAVA $message")
                        Log.d("-- debug: MyServerThread --", "Received message: $message")
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("-- debug: MyServerThread --", "Error: ${e.message}")
            }
        }
    }
}