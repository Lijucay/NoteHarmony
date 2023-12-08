package com.lijukay.yana.databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.lijukay.yana.items.Collection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CollectionDatabase(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $COLLECTION_TABLE ($COLUMN_TITLE TEXT PRIMARY KEY, $COLUMN_DESCRIPTION TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun getAllCollections(): MutableList<Collection> {
        val collections = mutableListOf<Collection>()
        CoroutineScope(Dispatchers.IO).launch {
            val db = readableDatabase
            val cursor = db.query(COLLECTION_TABLE, null, null, null, null, null, null)

            if (cursor != null) {
                val cITitle = cursor.getColumnIndex(COLUMN_TITLE)
                val cIDescription = cursor.getColumnIndex(COLUMN_DESCRIPTION)

                while (cursor.moveToNext()) {
                    val title = cursor.getString(cITitle)
                    val description = cursor.getString(cIDescription)

                    val collection = Collection(collectionTitle = title, collectionDescription = description)
                    collections.add(collection)
                }
                cursor.close()
            }
            db.close()
        }
        return collections
    }

    fun addToDatabase(collection: Collection): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE, collection.getCollectionTitle())
        values.put(COLUMN_DESCRIPTION, collection.getCollectionDescription())
        return try {
            db.insertOrThrow(COLLECTION_TABLE, null, values)
            db.close()
            true
        } catch (e: SQLiteConstraintException) {
            db.close()
            false
        }
    }

    companion object {
        private const val DATABASE_NAME = "collection_database"
        const val COLLECTION_TABLE = "collection_table"
        private const val DATABASE_VERSION = 1
        const val COLUMN_TITLE = "collection_title"
        private const val COLUMN_DESCRIPTION = "collection_description"
    }
}