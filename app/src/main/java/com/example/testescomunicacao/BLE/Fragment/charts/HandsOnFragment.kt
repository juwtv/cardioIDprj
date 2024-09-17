package com.example.testescomunicacao.BLE.Fragment.charts

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import com.example.testescomunicacao.R
import com.example.testescomunicacao.databinding.FragmentHandsOnBinding

class HandsOnFragment : Fragment() {
    private var _binding: FragmentHandsOnBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHandsOnBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun setHeartRate(value: String){
        Log.e("---> HandsOnFragment", "setHeartRate: $value")
        binding.heartRate.text = value
    }

    fun setHandsOnOld(value: Int){
        //Log.e("---> HandsOnFragment", "setHandsOnOld: $value")
        when(value){
            0 -> binding.handsWheel.setImageResource(com.example.shared.R.drawable.hands_on_none)
            1 -> binding.handsWheel.setImageResource(com.example.shared.R.drawable.hands_on_both)
        }
    }

    fun setHandsOn(value: Int){
        //Log.e("---> HandsOnFragment", "setHandsOn: $value")
        when(value){
            0 -> binding.handsWheel.setImageResource(com.example.shared.R.drawable.hands_on_none)
            1 -> binding.handsWheel.setImageResource(com.example.shared.R.drawable.hands_on_left)
            2 -> binding.handsWheel.setImageResource(com.example.shared.R.drawable.hands_on_right)
            3 -> binding.handsWheel.setImageResource(com.example.shared.R.drawable.hands_on_both)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
    }
}