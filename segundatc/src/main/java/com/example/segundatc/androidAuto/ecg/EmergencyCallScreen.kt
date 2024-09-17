package com.example.segundatc.androidAuto.ecg

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.annotations.ExperimentalCarApi
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.example.segundatc.androidAuto.MainCardioIDScreen
import com.example.shared.R

class EmergencyCallScreen(carContext: CarContext) : Screen(carContext) {

    init {
        // Registrar o callback para o botão de voltar
        carContext.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                screenManager.push(MainCardioIDScreen(carContext))
                Log.e("FINISH", "EmergencyCallScreen -> MainCardioIDScreen")
                finish()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @OptIn(ExperimentalCarApi::class)
    override fun onGetTemplate(): Template {

        // Obtém as informações do contato ICE
        val (iceContactName, iceContactPhoneNumber, iceContactPhotoUri) = getEmergencyContact()
            ?: Triple(null, "", null)

        val title = carContext.getString(R.string.emergency_title)

        // Criação dos itens do grid
        val gridItemList = ItemList.Builder()
            .apply {
                if (iceContactName != null) {
                    addItem(createGridItem(
                        title = iceContactName,
                        iconUri = iceContactPhotoUri,
                        iconResId = null,
                        contentId = "emergency_contact"
                    ))
                }
                addItem(createGridItem(
                    title = carContext.getString(R.string.sns_24),
                    iconUri = null,
                    iconResId = R.drawable.ic_sns24,
                    contentId = "sns_24"
                ))
                addItem(createGridItem(
                    title = carContext.getString(R.string.call_112),
                    iconUri = null,
                    iconResId = R.drawable.ic_e_call,
                    contentId = "e_call"
                ))
                addItem(createGridItem(
                    title = carContext.getString(R.string.contacts),
                    iconUri = null,
                    iconResId = R.drawable.ic_contacts,
                    contentId = "contacts"
                ))
            }
            .build()

        return GridTemplate.Builder()
            .setTitle(title)
            .setSingleList(gridItemList)
            .setHeaderAction(Action.BACK)
            .setItemSize(GridTemplate.ITEM_SIZE_LARGE)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createGridItem(title: String, iconUri: Uri?, iconResId: Int?, contentId: String): GridItem {
        val carIcon = if (iconUri != null) {
            CarIcon.Builder(IconCompat.createWithContentUri(iconUri)).build()
        } else {
            CarIcon.Builder(IconCompat.createWithResource(carContext, iconResId ?: R.drawable.ic_emergency_contact)).build()
        }

        return GridItem.Builder()
            .setTitle(title)
            .setImage(carIcon, GridItem.IMAGE_TYPE_LARGE)
            .setOnClickListener {
                when (contentId) {
                    "emergency_contact" -> {
                        val phoneNumber = getEmergencyContact()?.second
                        if (phoneNumber != null) {
                            CarToast.makeText(carContext, "${carContext.getString(R.string.call_emergency_contact)} $phoneNumber", CarToast.LENGTH_SHORT).show()
                            makeCall(phoneNumber) // Comentar esta linha para não fazer a chamada
                        } else {
                            Log.e("EmergencyCallScreen", "Nenhum contacto de emergência encontrado.")
                        }
                    }
                    "sns_24" -> {
                        Log.e("EmergencyCallScreen", "Calling SNS24: 808242424")
                        CarToast.makeText(carContext, carContext.getString(R.string.call_sns24), CarToast.LENGTH_SHORT).show()
                        makeCall("926087661") // Liga para o telemóvel da Joana Pereira
                    }
                    "e_call" -> {
                        CarToast.makeText(carContext, carContext.getString(R.string.call_ecall), CarToast.LENGTH_SHORT).show()
                        Log.e("EmergencyCallScreen", "Calling eCall: 112")
                        makeCall("965488225") // Liga para o telemóvel do Engenheiro André Lourenço
                    }
                    "contacts" -> {
                        CarToast.makeText(carContext, carContext.getString(R.string.open_contacts), CarToast.LENGTH_SHORT).show()
                        openDialerInDHU()
                    }
                }
            }
            .build()
    }

    @SuppressLint("Range")
    private fun getEmergencyContact(): Triple<String?, String, Uri?>? {
        val resolver = carContext.contentResolver

        // 1. Obter todos os IDs dos grupos "ICE"
        val groupCursor = resolver.query(
            ContactsContract.Groups.CONTENT_URI,
            arrayOf(ContactsContract.Groups._ID),
            "${ContactsContract.Groups.TITLE} = ?",
            arrayOf("ICE"),  // Nome do grupo de emergência
            null
        )

        val groupIds = mutableListOf<String>()
        groupCursor?.use {
            while (it.moveToNext()) {
                val groupId = it.getString(it.getColumnIndex(ContactsContract.Groups._ID))
                groupIds.add(groupId)
            }
        }

        if (groupIds.isEmpty()) {
            Log.e("EmergencyCallScreen", "Nenhum grupo de Contactos de Emergência (ICE) encontrado.")
            return null
        }

        // 2. Consultar os contatos que pertencem a todos os grupos "ICE"
        for (groupId in groupIds) {
            // Primeiro, obtemos todos os IDs de contatos que pertencem a este grupo
            val contactIdCursor = resolver.query(
                ContactsContract.Data.CONTENT_URI,
                arrayOf(ContactsContract.Data.CONTACT_ID),
                "${ContactsContract.Data.MIMETYPE} = ? AND ${ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID} = ?",
                arrayOf(ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE, groupId),
                null
            )

            val contactIds = mutableSetOf<String>()
            contactIdCursor?.use {
                while (it.moveToNext()) {
                    val contactId = it.getString(it.getColumnIndex(ContactsContract.Data.CONTACT_ID))
                    contactIds.add(contactId)
                }
            }

            // Para cada ID de contacto encontrado, buscamos o número de telefone, nome e foto
            for (contactId in contactIds) {
                val phoneCursor = resolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_URI
                    ),
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                    arrayOf(contactId),
                    null
                )

                phoneCursor?.use {
                    if (it.moveToNext()) {
                        val phoneNumber = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        val name = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        val photoUri = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))?.let { uriString ->
                            Uri.parse(uriString)
                        }

                        return Triple(name, phoneNumber, photoUri)
                    }
                }
            }
        }

        Log.e("EmergencyCallScreen", "Nenhum contacto encontrado nos grupos ICE.")
        return null
    }

    private fun makeCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        if (intent.resolveActivity(carContext.packageManager) != null) {
            carContext.startActivity(intent)
        } else {
            Log.e("EmergencyCallScreen", "No application available to handle the call intent.")
        }
    }

    private fun openDialerInDHU() {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        if (intent.resolveActivity(carContext.packageManager) != null) {
            carContext.startActivity(intent)
        } else {
            Log.e("EmergencyCallScreen", "No application available to handle the dialer intent.")
        }
    }


}