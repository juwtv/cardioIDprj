package com.example.segundatc.fragments.boundService

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import com.example.segundatc.R
import com.example.segundatc.ViewModel.SharedViewModel
import com.example.segundatc.databinding.FragmentBoundServicesReceiverBinding


class BoundServicesReceiverFragment : Fragment() {

    private var _binding: FragmentBoundServicesReceiverBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_bound_services_receiver, container, false)
        _binding = FragmentBoundServicesReceiverBinding.inflate(inflater, container, false)

        sharedViewModel = SharedViewModel.getInstance()

        sharedViewModel.messageBS.observe(viewLifecycleOwner, Observer { message ->
            binding.textViewMessage.text = message
        })

        return binding.root
    }

}