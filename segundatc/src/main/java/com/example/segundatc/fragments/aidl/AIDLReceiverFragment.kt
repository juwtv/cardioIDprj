package com.example.segundatc.fragments.aidl

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.segundatc.ViewModel.SharedViewModel
import com.example.segundatc.databinding.FragmentAidlReceiverBinding

class AIDLReceiverFragment : Fragment() {

    private var _binding: FragmentAidlReceiverBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAidlReceiverBinding.inflate(inflater, container, false)

        binding.btnReceiveDataAidl.setOnClickListener {
            // Simulação de receber dados do serviço
            Toast.makeText(requireContext(), "Waiting for data...", Toast.LENGTH_SHORT).show()
        }

        sharedViewModel = SharedViewModel.getInstance()

        sharedViewModel.messageAIDL.observe(viewLifecycleOwner, Observer { message ->
            binding.textViewMessage.text = message
        })

        return binding.root
    }
}