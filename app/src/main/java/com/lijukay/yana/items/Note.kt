package com.lijukay.yana.items

class Note(
    private var title: String,
    private var content: String,
    private var collectionName: String
) {
    fun getTitle(): String {
        return title
    }

    fun getContent(): String {
        return content
    }

    fun getCollectionName(): String {
        return collectionName
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun setContent(content: String) {
        this.content = content
    }

    fun setCollectionName(collectionName: String) {
        this.collectionName = collectionName
    }
}