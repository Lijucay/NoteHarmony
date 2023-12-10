package com.lijukay.noteharmony.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lijukay.noteharmony.databases.Note
import com.lijukay.noteharmony.databinding.ItemViewBinding

class NotesAdapter(
    private var notesList: MutableList<Note> = mutableListOf()
): RecyclerView.Adapter<NotesAdapter.NotesListViewHolder>() {
    inner class NotesListViewHolder(val binding: ItemViewBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesListViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotesListViewHolder, position: Int) {
        val currentNoteItem = notesList[position]
        val noteTitle = currentNoteItem.noteName
        val noteContent = currentNoteItem.noteContent

        val titleTextView = holder.binding.titleTextView
        val contentTextView = holder.binding.contentTextView

        titleTextView.text = noteTitle
        contentTextView.text = noteContent
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    fun addNote(note: Note) {
        notesList.add(note)
    }
}