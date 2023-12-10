package com.lijukay.yana.databases

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collection")
    fun getAllCollections(): MutableList<Collection>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCollection(collection: Collection): Long

    @Delete
    fun deleteCollection(collection: Collection)

    @Query("DELETE FROM collection")
    fun deleteAll()
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM note WHERE collectionKey = :collectionName AND isHidden = :isHidden")
    fun getNotes(collectionName: String, isHidden: Int = 0): MutableList<Note>

    @Query("SELECT * FROM note WHERE isHidden = :isHidden")
    fun getHiddenNotes(isHidden: Int = 1): MutableList<Note>

    @Insert
    fun insertNote(note: Note): Long

    @Delete
    fun deleteNote(note: Note)

    @Query("DELETE FROM note")
    fun deleteAll()
}