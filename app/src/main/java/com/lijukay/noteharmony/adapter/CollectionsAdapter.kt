package com.lijukay.noteharmony.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lijukay.noteharmony.databases.Collection
import com.lijukay.noteharmony.databinding.ItemViewBinding
import com.lijukay.noteharmony.utils.interfaces.OnClickInterface

/**
 * This class represents an adapter for the collection recyclerview
 * @param collectionsList A mutable list of the collection item
 * @param onClickListener An interface to handle onClicks
 * @see OnClickInterface
 * @see Collection
 */
class CollectionsAdapter(
    private var collectionsList: MutableList<Collection> = mutableListOf(),
    private val onClickListener: OnClickInterface?
): RecyclerView.Adapter<CollectionsAdapter.CollectionsListViewHolder>() {

    inner class CollectionsListViewHolder(val binding: ItemViewBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onClickListener?.onClick(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionsListViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionsListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CollectionsListViewHolder, position: Int) {
        val currentCollectionItem = collectionsList[position]
        val collectionTitle = currentCollectionItem.collectionName
        val collectionDescription = currentCollectionItem.collectionDescription

        val titleTextView = holder.binding.titleTextView
        val descriptionTextView = holder.binding.contentTextView

        titleTextView.text = collectionTitle
        descriptionTextView.text = collectionDescription
    }

    override fun getItemCount(): Int {
        return collectionsList.size
    }

    fun addCollection(collection: Collection) {
        collectionsList.add(collection)
    }
}