package com.example.segundatc.fragments.intent

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.segundatc.R
import com.example.segundatc.ViewModel.SharedViewModel
import com.example.segundatc.databinding.FragmentIntentsReceiverBinding


class IntentsReceiverFragment : Fragment() {

    private lateinit var binding: FragmentIntentsReceiverBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntentsReceiverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = SharedViewModel.getInstance()

        sharedViewModel.messageIntent.observe(viewLifecycleOwner) { message ->
            binding.tvMessage.text = message
        }
    }

    override fun onResume() {
        super.onResume()
    }

}