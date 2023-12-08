package com.lijukay.yana.items

class Collection(
    private var collectionTitle: String,
    private var collectionDescription: String
) {
    fun getCollectionTitle(): String {
        return collectionTitle
    }

    fun setCollectionTitle(collectionTitle: String) {
        this.collectionTitle = collectionTitle
    }

    fun getCollectionDescription(): String {
        return collectionDescription
    }

    fun setCollectionDescription(collectionDescription: String) {
        this.collectionDescription = collectionDescription
    }
}