package com.lijukay.noteharmony.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.room.Room
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lijukay.noteharmony.R
import com.lijukay.noteharmony.adapter.NotesAdapter
import com.lijukay.noteharmony.databases.Note
import com.lijukay.noteharmony.databases.NoteDao
import com.lijukay.noteharmony.databases.NoteHarmonyDatabase
import com.lijukay.noteharmony.databinding.ActivityNotesBinding
import com.lijukay.noteharmony.dialogs.CreateNoteDialog
import com.lijukay.noteharmony.dialogs.ShowNoteDialog
import com.lijukay.noteharmony.utils.interfaces.OnClickInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotesActivity : AppCompatActivity(), OnClickInterface {
    private lateinit var binding: ActivityNotesBinding
    private lateinit var noteDao: NoteDao
    private lateinit var notes: MutableList<Note>
    private lateinit var collectionName: String
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        intent?.let {
            collectionName = intent.getStringExtra("collection name") ?: getString(R.string.no_collection_name_found)
        }

        val materialToolbar = binding.materialToolbar
        val bottomAppBar = binding.bottomAppBar
        bottomAppBar.menu.findItem(R.id.invisible_menu_item).isVisible = false

        bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.delete_menu_icon -> {
                    showConfirmDeletionDialog()
                    return@setOnMenuItemClickListener true
                }
            }
            return@setOnMenuItemClickListener false
        }

        materialToolbar.subtitle = collectionName
        setSupportActionBar(materialToolbar)

        lifecycleScope.launch(Dispatchers.IO) {
            val noteHarmonyDatabase = Room.databaseBuilder(
                context = applicationContext,
                klass = NoteHarmonyDatabase::class.java,
                name = "note_harmony_database"
            ).build()
            noteDao = noteHarmonyDatabase.noteDao()
            notes = noteDao.getNotes(collectionName)
            notesAdapter = NotesAdapter(notes, this@NotesActivity)

            withContext(context = Dispatchers.Main) {
                setupUI()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showConfirmDeletionDialog() {
        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
            .setTitle(getString(R.string.confirm_deletion_title))
            .setMessage(getString(R.string.confirm_deletion_message, "all notes"))
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
                lifecycleScope.launch(Dispatchers.IO) {
                    val noteHarmonyDatabase = Room.databaseBuilder(
                        context = applicationContext,
                        klass = NoteHarmonyDatabase::class.java,
                        name = "note_harmony_database"
                    ).build()
                    val noteDao = noteHarmonyDatabase.noteDao()
                    noteDao.deleteAll()
                    notes.clear()
                    withContext(Dispatchers.Main) {
                        notesAdapter.notifyDataSetChanged()
                        toggleVisibility(notesAdapter.itemCount)
                    }
                }
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setIcon(R.drawable.round_delete_24)
            .show()
    }

    private fun setupUI() {
        val notesRecyclerView = binding.noteRecyclerView
        notesRecyclerView.layoutManager = LinearLayoutManager(this)
        notesRecyclerView.adapter = notesAdapter

        toggleVisibility(notesAdapter.itemCount)

        val addFAB = binding.addNoteButton
        addFAB.setOnClickListener {
            CreateNoteDialog(
                context = this@NotesActivity,
                notesAdapter = notesAdapter,
                collectionName = collectionName,
                null,
                null
            ).show()
        }
    }

    fun toggleVisibility(itemCount: Int) {
        val noNotesInfoTextView = binding.noNotesInfo
        val notesRecyclerView = binding.noteRecyclerView

        if (itemCount != 0) {
            noNotesInfoTextView.visibility = View.GONE
            notesRecyclerView.visibility = View.VISIBLE
        } else {
            noNotesInfoTextView.visibility = View.VISIBLE
            notesRecyclerView.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settings_menu_item -> {
                startActivity(Intent(this@NotesActivity, SettingsActivity::class.java))
                return true
            }
        }
        return false
    }

    override fun onClick(position: Int) {
        ShowNoteDialog(
            this,
            notes[position].noteName,
            notes[position].noteContent
        ).show()
    }
}