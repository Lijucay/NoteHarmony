package com.lijukay.yana.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lijukay.yana.databinding.CollectionItemViewBinding
import com.lijukay.yana.items.Collection

class CollectionsAdapter(
    private var collectionsList: MutableList<Collection> = mutableListOf()
): RecyclerView.Adapter<CollectionsAdapter.CollectionsListViewHolder>() {

    inner class CollectionsListViewHolder(val binding: CollectionItemViewBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionsListViewHolder {
        val binding = CollectionItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionsListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollectionsListViewHolder, position: Int) {
        val currentCollectionItem = collectionsList[position]
        val collectionTitle = currentCollectionItem.getCollectionTitle()
        val collectionDescription = currentCollectionItem.getCollectionDescription()

        val titleTextView = holder.binding.titleTextView
        val descriptionTextView = holder.binding.descriptionTextView

        titleTextView.text = collectionTitle
        descriptionTextView.text = collectionDescription
    }

    override fun getItemCount(): Int {
        return collectionsList.size
    }
}