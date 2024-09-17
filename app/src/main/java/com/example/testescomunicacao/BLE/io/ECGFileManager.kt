package com.example.testescomunicacao.BLE.io

import android.os.Environment
import com.cardioid.cardioid_ble.file.FileManager
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*


class ECGFileManager(rootDir: File, private val samplingRate: Int) {
    private var fileManager = FileManager(rootDir)

    fun createFile(fileName: String, deviceName: String){
        val current = LocalDateTime.now(ZoneOffset.UTC)
        fileManager.addFolder(DIRECTORY)
        val isCreated = fileManager.createFile(fileName)
        if(isCreated){
            fileManager.writeFile(header(current.toString(), deviceName))
        }
    }

    fun saveEcg(ecg: IntArray, hand: Int, index: Int){
        val builder = StringBuilder()
        val split = 1.toDouble() / samplingRate.toDouble()
        ecg.forEachIndexed { i, value ->
            val time = (split * (index+1+i).toDouble() * 1000).toLong().toString() + ""
            builder.append(
                Formatter().format(
                    ECG_TEMPLATE,
                    hand,
                    value,
                    time
                ).toString())
            builder.append("\n")
        }
        fileManager.writeFile(builder.toString())
    }

    private fun header(date: String, deviceName: String) : String {
        val builder = StringBuilder()
        builder.append("#Date:= $date")
        builder.append("\n")
//        builder.append("#Device:= $deviceName")
//        builder.append("\n")
        builder.append("#Sampling Rate(Hz):= $samplingRate")
        builder.append("\n")
        builder.append("#Labels:= LOD\tECG\tTS")
        builder.append("\n")
        return builder.toString()
    }

    fun changeRootDir(fileName: String) {
        fileManager.changeRootDir(fileName, Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS)!!)
    }
    fun renameFile(fileName: String) {
        fileManager.renameFile(fileName)
    }

    fun deleteFile() {
        fileManager.deleteFile(1)
        fileManager.deleteFile()
    }

    fun getFile(): File? {
        fileManager.createFile("temp", 1)
        copy(fileManager.getFile(), fileManager.getFile(1))
        return fileManager.getFile(1)
    }

    fun copy(src: File?, dst: File?) {
        val ins: InputStream = FileInputStream(src)
        try {
            val out: OutputStream = FileOutputStream(dst)
            try {
                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len: Int
                while (ins.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
            } finally {
                out.close()
            }
        } finally {
            ins.close()
        }
    }

    companion object {
        private const val DIRECTORY = "/ECGBiometrics"
        // ECG template to write on every line;
        const val ECG_TEMPLATE = "%s\t%s\t%s"
    }
}