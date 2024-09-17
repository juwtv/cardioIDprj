package com.example.segundatc.fragments.contentProvider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.example.segundatc.ViewModel.SharedViewModel
import com.example.shared.SharedConstants.CONTENT_URI
import com.example.shared.SharedConstants.TABLE_NAME

class MyContentProvider : ContentProvider() {

    /*companion object {
        const val AUTHORITY = "com.example.segundatc.fragments.contentProvider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/${MyDatabaseHelper.TABLE_NAME}")
    }*/

    private lateinit var dbHelper: MyDatabaseHelper
    private val sharedViewModel = SharedViewModel.getInstance()

    override fun onCreate(): Boolean {
        dbHelper = MyDatabaseHelper(context as Context)
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHelper.writableDatabase
        val id = db.insert(TABLE_NAME, null, values)
        if (id == -1L) {
            Log.e("MyContentProvider", "Insert falhou")
            return null
        }
        context?.contentResolver?.notifyChange(uri, null)

        // Notify SharedViewModel
        values?.getAsString("key")?.let {
            Log.e("ChamaViewModel", "MyCP")
            sharedViewModel.setMessageCP(it)
        }

        return ContentUris.withAppendedId(CONTENT_URI, id)
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val db = dbHelper.readableDatabase
        return db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val db = dbHelper.writableDatabase
        val rowsUpdated = db.update(TABLE_NAME, values, selection, selectionArgs)
        context?.contentResolver?.notifyChange(uri, null)
        return rowsUpdated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs)
        context?.contentResolver?.notifyChange(uri, null)
        return rowsDeleted
    }

    override fun getType(uri: Uri): String? {
        return null
    }
}