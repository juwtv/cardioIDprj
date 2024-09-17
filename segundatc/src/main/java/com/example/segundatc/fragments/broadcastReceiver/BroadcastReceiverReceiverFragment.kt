package com.example.segundatc.fragments.broadcastReceiver

import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.segundatc.ViewModel.SharedViewModel
import com.example.segundatc.databinding.FragmentBroadcastReceiverReceiverBinding
import com.example.shared.SharedConstants.ACTION_SEND_MESSAGE_BD

class BroadcastReceiverReceiverFragment : Fragment() {

    private var _binding: FragmentBroadcastReceiverReceiverBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel
    //private lateinit var myBroadcastReceiver: MyBroadcastReceiver
    private var myBroadcastReceiver: MyBroadcastReceiver? = null
    //private var isReceiverRegistered = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBroadcastReceiverReceiverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = SharedViewModel.getInstance()

        // Observe the LiveData from the SharedViewModel
        sharedViewModel.messageBC.observe(viewLifecycleOwner, Observer { message ->
            binding.textViewMessage.text = message
        })

        // Initialize and register the BroadcastReceiver
        //myBroadcastReceiver = MyBroadcastReceiver()
        myBroadcastReceiver = MyBroadcastReceiver.getInstance()

        /*val intentFilter = IntentFilter(ACTION_SEND_MESSAGE_BD)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context?.registerReceiver(myBroadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        }
        isReceiverRegistered = true*/
    }

    override fun onStart() {
        super.onStart()
        // Register the BroadcastReceiver when the fragment is visible
        if (!MyBroadcastReceiver.isRegistered) {
            val intentFilter = IntentFilter(ACTION_SEND_MESSAGE_BD)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context?.registerReceiver(
                    myBroadcastReceiver,
                    intentFilter,
                    Context.RECEIVER_EXPORTED
                )
                MyBroadcastReceiver.isRegistered = true
            }
        }
        //isReceiverRegistered = true
    }

    override fun onStop() {
        super.onStop()
        // Unregister the BroadcastReceiver when the fragment is no longer visible
        if (MyBroadcastReceiver.isRegistered) {
            try {
                context?.unregisterReceiver(myBroadcastReceiver)
                MyBroadcastReceiver.isRegistered = false
            } catch (e: IllegalArgumentException) {
                Log.e("BroadcastReceiverFragment", "Receiver not registered", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Ensure the receiver is unregistered to avoid leaks
        if (MyBroadcastReceiver.isRegistered) {
            try {
                context?.unregisterReceiver(myBroadcastReceiver)
                MyBroadcastReceiver.isRegistered = false
            } catch (e: IllegalArgumentException) {
                Log.e("BroadcastReceiverFragment", "Receiver not registered", e)
            }
        }
    }
}