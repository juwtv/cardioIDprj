package com.example.testescomunicacao.BLE.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.testescomunicacao.databinding.FragmentDialogFileBinding
import java.text.SimpleDateFormat
import java.util.*

interface DialogFileCallback {
    fun onSubmit(fileName: String)
    fun onCancel()
}
class DialogFileFragment(private val dialogFileCallback: DialogFileCallback) : DialogFragment() {
    private var _binding: FragmentDialogFileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireDialog().setCanceledOnTouchOutside(false)
        _binding = FragmentDialogFileBinding.inflate(inflater, container, false)
        binding.save.setOnClickListener { onSave() }
        binding.cancel.setOnClickListener {
            dialogFileCallback.onCancel()
            dismiss()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onSave() {
        var filename = binding.fileName.text.toString()
        if(filename.isEmpty()){
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale.US)
            sdf.timeZone = SimpleTimeZone(0, ID_TIMEZONE)
            filename = sdf.format(System.currentTimeMillis())
        }
        dialogFileCallback.onSubmit(filename)
        dismiss()
    }

    companion object {
        const val DATE_FORMAT = "yyyy_MM_dd_HH_mm_ss"
        const val ID_TIMEZONE = "GMT"
    }
}