package com.example.segundatc.fragments.contentProvider

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.segundatc.ViewModel.SharedViewModel
import com.example.segundatc.databinding.FragmentContentProviderReceiverBinding
import com.example.shared.SharedConstants.CONTENT_URI

class ContentProviderReceiverFragment : Fragment() {

    private var _binding: FragmentContentProviderReceiverBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContentProviderReceiverBinding.inflate(inflater, container, false)

        binding.btnReceiveData.setOnClickListener {
            Toast.makeText(requireContext(), "Waiting for data...", Toast.LENGTH_SHORT).show()
        }
        sharedViewModel = SharedViewModel.getInstance()

        sharedViewModel.messageCP.observe(viewLifecycleOwner, Observer { message ->
            binding.textViewData.text = message
        })

        return binding.root
    }
}