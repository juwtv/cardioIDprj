package com.example.testescomunicacao.fragments.contentProvider

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shared.SharedConstants.CONTENT_URI
import com.example.testescomunicacao.databinding.FragmentContentProviderSenderBinding

class ContentProviderSenderFragment : Fragment() {

    private var _binding: FragmentContentProviderSenderBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentContentProviderSenderBinding.inflate(inflater, container, false)

        binding.btnSendData.setOnClickListener {
            val message = binding.messageToSend.text.toString()
            sendDataWithContentProvider(requireContext(), message)
        }

        return binding.root
    }

    private fun sendDataWithContentProvider(context: Context, data: String) {
        val contentValues = ContentValues().apply {
            put("key", data)
        }
        context.contentResolver.insert(CONTENT_URI, contentValues)
        Log.d("CONTENT PROVIDER", "Mensagem Enviada: $data")
    }
}