package com.example.testescomunicacao.fragments.socket

import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.testescomunicacao.R
import java.io.DataOutputStream
import java.io.PrintWriter
import java.net.Socket
import java.util.regex.Pattern

class MessageSender(private var view: View) : AsyncTask<String, Void, Void>() {

    private var socket: Socket? = null
    private var dos: DataOutputStream? = null
    private var pw: PrintWriter? = null

    //private var ip: String = "192.168.1.89" // pc: cmd > ipconfig
    private var ip: String = "127.0.0.1" // Use localhost

    var etIP: EditText = view.findViewById(R.id.ipSocketToSend)

    override fun doInBackground(vararg params: String?): Void? {

        var message : String = params[0].toString()
        //var usableIp = etIP.getText().toString()
        var usableIp = ip
        //Log.d("MessageSender", "$usableIp:7800 message: $message")
        Log.d("debug: MessageSender", "$usableIp:7802 message: $message")

        /*if (isValidIPAddress(usableIp)) {
            try {
                //socket = Socket(ip, 7800)
                socket = Socket(usableIp, 7802) // send
                pw = PrintWriter(socket!!.getOutputStream())
                pw!!.write(message)
                pw!!.flush()
                pw!!.close()
                socket!!.close()
            } catch (e: Exception) { e.printStackTrace() }
        } else {
            Toast.makeText(view.context, "Invalid IP address", Toast.LENGTH_SHORT).show()
        }*/
        try {
            socket = Socket(usableIp, 7802) // Send to localhost
            pw = PrintWriter(socket!!.getOutputStream())
            pw!!.write(message)
            pw!!.flush()
            pw!!.close()
            socket!!.close()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("debug: MessageSender", "Error: ${e.message}")
        }

        return null
    }

    fun isValidIPAddress(ip: String): Boolean {
        val ipv4Pattern = Pattern.compile(
            "^(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\." +
                    "(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\." +
                    "(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\." +
                    "(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])$"
        )

        val ipv6Pattern = Pattern.compile(
            "(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|" +
                    "([0-9a-fA-F]{1,4}:){1,7}:|" +
                    "([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|" +
                    "([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|" +
                    "([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|" +
                    "([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|" +
                    "([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|" +
                    "[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|" +
                    ":((:[0-9a-fA-F]{1,4}){1,7}|:)|" +
                    "fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|" +
                    "::(ffff(:0{1,4}){0,1}:){0,1}" +
                    "((25[0-5]|(2[0-4]|1{0,1}[0-9])?[0-9])\\.){3,3}" +
                    "(25[0-5]|(2[0-4]|1{0,1}[0-9])?[0-9])|" +
                    "([0-9a-fA-F]{1,4}:){1,4}:" +
                    "((25[0-5]|(2[0-4]|1{0,1}[0-9])?[0-9])\\.){3,3}" +
                    "(25[0-5]|(2[0-4]|1{0,1}[0-9])?[0-9]))"
        )

        return ipv4Pattern.matcher(ip).matches() || ipv6Pattern.matcher(ip).matches()
    }
}