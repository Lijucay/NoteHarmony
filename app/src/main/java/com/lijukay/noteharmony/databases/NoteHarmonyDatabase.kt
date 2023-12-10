package com.lijukay.noteharmony.databases

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Note::class, Collection::class], version = 1)
abstract class NoteHarmonyDatabase : RoomDatabase() {
    abstract fun collectionDao(): CollectionDao
    abstract fun noteDao(): NoteDao
}