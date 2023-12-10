package com.lijukay.yana.databases

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * A class that represents the collection item
 * @param collectionName The name (title) of the collection
 * @param collectionDescription A description of what the collection is
 */
@Entity
data class Collection(
    @PrimaryKey val collectionName: String, val collectionDescription: String
)

@Entity(
    tableName = "note",
    foreignKeys = [ForeignKey(
        entity = Collection::class,
        parentColumns = ["collectionName"],
        childColumns = ["collectionKey"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Note(
    @PrimaryKey val noteName: String,
    val noteContent: String,
    val creationDate: String,
    @ColumnInfo(defaultValue = "0") val isHidden: Int,
    val collectionKey: String
)