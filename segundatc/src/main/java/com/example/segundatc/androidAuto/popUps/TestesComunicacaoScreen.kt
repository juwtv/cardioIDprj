package com.example.segundatc.androidAuto.popUps

import android.util.Log
import com.example.segundatc.ViewModel.SharedViewModel

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Row
import androidx.car.app.model.Template
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.lifecycle.Observer
import com.example.segundatc.androidAuto.MyLifecycleOwner
import com.example.shared.R

class TestesComunicacaoScreen(carContext: CarContext) : Screen(carContext) {

    private val sharedViewModel = SharedViewModel.getInstance()
    private val lifecycleOwner = MyLifecycleOwner()
    private var messageCP: String = ""
    private var messageBC: String = ""
    private var messageIntent: String = ""
    private var messageAIDL: String = ""
    private var messageBS: String = ""
    private var messageSocket: String = ""

    private var messageHeartRate: String = ""

    init {
        Log.d("DEBUG AUTO", "AndroidAutoScreen initialized")
        // Configura o estado inicial do LifecycleOwner
        lifecycleOwner.doOnResume()

        sharedViewModel.messageHearRate.observe(lifecycleOwner, Observer { newMessage ->
            messageHeartRate = newMessage
            invalidate()
        })

        sharedViewModel.messageCP.observe(lifecycleOwner, Observer { newMessage ->
            messageCP = newMessage
            invalidate()
        })
        sharedViewModel.messageBC.observe(lifecycleOwner, Observer { newMessage ->
            messageBC = newMessage
            invalidate()
        })
        sharedViewModel.messageIntent.observe(lifecycleOwner, Observer { newMessage ->
            messageIntent = newMessage
            invalidate()
        })
        sharedViewModel.messageAIDL.observe(lifecycleOwner, Observer { newMessage ->
            messageAIDL = newMessage
            invalidate()
        })
        sharedViewModel.messageSocket.observe(lifecycleOwner, Observer { newMessage ->
            Log.e("DEBUG AUTO", "SOCKET RECEIVED: $newMessage")
            messageSocket = newMessage
            invalidate()
        })
        sharedViewModel.messageBS.observe(lifecycleOwner, Observer { newMessage ->
            messageBS = newMessage
            Log.e("DEBUG AUTO", "BS RECEIVED: $newMessage")
            invalidate()
        })
    }

    override fun onGetTemplate(): Template {
        Log.d("DEBUG AUTO", "onGetTemplate called")
        // Atualiza o estado do LifecycleOwner
        lifecycleOwner.doOnResume()

        Log.d("DEBUG AUTO", "Current messageSocket: $messageSocket")

        val titleTemplate = carContext.getString(R.string.teste_comun_title)
        val titleHeartRate = carContext.getString(R.string.titleHeartRate)
        val titleCP = carContext.getString(R.string.titleCP)
        val titleBC = carContext.getString(R.string.titleBC)
        val titleIntent = carContext.getString(R.string.titleIntent)
        val titleAIDL = carContext.getString(R.string.titleAIDL)
        val titleBS = carContext.getString(R.string.titleBS)
        val titleSocket = carContext.getString(R.string.titleSocket)
        val titleBack = carContext.getString(R.string.titleBack)

        val itemListBuilder = ItemList.Builder()
            .addItem(
                Row.Builder()
                    .setTitle(titleHeartRate)
                    .addText(messageHeartRate)
                    .build()
            )
            .addItem(
                Row.Builder()
                    .setTitle(titleCP)
                    .addText(messageCP)
                    .build()
            )
            .addItem(
                Row.Builder()
                    .setTitle(titleBC)
                    .addText(messageBC)
                    .build()
            )
            .addItem(
                Row.Builder()
                    .setTitle(titleIntent)
                    .addText(messageIntent)
                    .build()
            )
            .addItem(
                Row.Builder()
                    .setTitle(titleAIDL)
                    .addText(messageAIDL)
                    .build()
            )
            .addItem(
                Row.Builder()
                    .setTitle(titleBS)
                    .addText(messageBS)
                    .build()
            )
            .addItem(
                Row.Builder()
                    .setTitle(titleSocket)
                    .addText(messageSocket)
                    .build()
            )

        return ListTemplate.Builder()
            .setSingleList(itemListBuilder.build())
            .setTitle(titleTemplate)
            .setHeaderAction(Action.BACK)
            .build()
    }
}