package com.lijukay.yana.databases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotesDatabase(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    companion object {
        private const val DATABASE_NAME = "notes_database"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "notes_table"
        private const val COLUMN_TITLE = "notes_title"
        private const val COLUMN_NOTE = "note"
        private const val COLUMN_DATE = "creation_date"
        private const val COLUMN_COLLECTION_TITLE = "collection_title"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME ($COLUMN_TITLE TEXT PRIMARY KEY, $COLUMN_NOTE TEXT, $COLUMN_DATE DATE, $COLUMN_COLLECTION_TITLE TEXT, FOREIGN KEY ($COLUMN_COLLECTION_TITLE) REFERENCES ${CollectionDatabase.COLLECTION_TABLE}(${CollectionDatabase.COLUMN_TITLE}))")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    //TODO: Consider changing to room database
}