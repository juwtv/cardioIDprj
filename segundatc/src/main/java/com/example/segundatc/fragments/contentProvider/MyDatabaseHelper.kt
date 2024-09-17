package com.example.segundatc.fragments.contentProvider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.shared.SharedConstants.COLUMN_ID
import com.example.shared.SharedConstants.COLUMN_KEY
import com.example.shared.SharedConstants.TABLE_NAME

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, \"$COLUMN_KEY\" TEXT)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }


    companion object {
        private const val DATABASE_NAME = "testDatabase.db"
        private const val DATABASE_VERSION = 1
        /*const val TABLE_NAME = "messages"
        const val COLUMN_ID = "id"
        const val COLUMN_KEY = "key"*/
    }
}