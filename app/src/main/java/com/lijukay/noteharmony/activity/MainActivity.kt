package com.lijukay.noteharmony.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.room.Room
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lijukay.noteharmony.R
import com.lijukay.noteharmony.adapter.CollectionsAdapter
import com.lijukay.noteharmony.adapter.NotesAdapter
import com.lijukay.noteharmony.databases.Collection
import com.lijukay.noteharmony.databases.NoteHarmonyDatabase
import com.lijukay.noteharmony.databinding.ActivityMainBinding
import com.lijukay.noteharmony.databinding.DialogHiddenNotesBinding
import com.lijukay.noteharmony.dialogs.CreateCollectionDialog
import com.lijukay.noteharmony.utils.interfaces.OnClickInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), OnClickInterface {
    private lateinit var binding: ActivityMainBinding
    private lateinit var collections: MutableList<Collection>
    private lateinit var collectionsAdapter: CollectionsAdapter
    private lateinit var collectionsRecyclerView: RecyclerView

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val materialToolbar = binding.materialToolbar
        val bottomAppBar = binding.bottomAppBar

        bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.delete_menu_icon -> {
                    showConfirmDeletionDialog()
                    return@setOnMenuItemClickListener true
                }

                R.id.invisible_menu_item -> {
                    showVerificationDialog {
                        showHiddenNotesDialog()
                    }
                }
            }
            return@setOnMenuItemClickListener false
        }

        setSupportActionBar(materialToolbar)

        lifecycleScope.launch(Dispatchers.IO) {
            val noteHarmonyDatabase = Room.databaseBuilder(
                context = applicationContext,
                klass = NoteHarmonyDatabase::class.java,
                name = "note_harmony_database"
            ).build()
            val collectionDao = noteHarmonyDatabase.collectionDao()
            collections = collectionDao.getAllCollections()
            collectionsAdapter = CollectionsAdapter(collections, this@MainActivity)

            withContext(context = Dispatchers.Main) {
                setupUI()
            }
        }
    }

    private fun setupUI() {
        collectionsRecyclerView = binding.noteCollectionRecyclerView
        collectionsRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        collectionsRecyclerView.adapter = collectionsAdapter

        toggleVisibility(collectionsAdapter.itemCount)

        val addFAB = binding.addCollectionButton
        addFAB.setOnClickListener {
            CreateCollectionDialog(
                context = this@MainActivity,
                adapter = collectionsAdapter
            ).show()
        }
    }

    private fun showHiddenNotesDialog() {
        val dialog = BottomSheetDialog(this@MainActivity)
        val dialogBinding = DialogHiddenNotesBinding.inflate(layoutInflater)
        val hiddenNotesRecyclerView = dialogBinding.hiddenNotesRecyclerView

        lifecycleScope.launch(context = Dispatchers.IO) {
            val noteHarmonyDatabase = Room.databaseBuilder(
                context = applicationContext,
                klass = NoteHarmonyDatabase::class.java,
                name = "note_harmony_database"
            ).build()
            val noteDao = noteHarmonyDatabase.noteDao()
            val hiddenNotes = noteDao.getHiddenNotes()

            withContext(context = Dispatchers.Main) {
                val adapter = NotesAdapter(notesList = hiddenNotes)
                hiddenNotesRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                hiddenNotesRecyclerView.adapter = adapter

                dialog.setContentView(dialogBinding.root)
                val bottomSheetBehavior = BottomSheetBehavior.from(dialogBinding.root.parent as View)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                dialog.show()
            }
        }
    }

    private fun showVerificationDialog(callback: () -> Unit) {
        callback()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settings_menu_item -> {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                return true
            }
        }
        return false
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showConfirmDeletionDialog() {
        MaterialAlertDialogBuilder(this, com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
            .setTitle(getString(R.string.confirm_deletion_title))
            .setMessage(getString(R.string.confirm_deletion_message, "all collections"))
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
                lifecycleScope.launch(Dispatchers.IO) {
                    val noteHarmonyDatabase = Room.databaseBuilder(
                        context = applicationContext,
                        klass = NoteHarmonyDatabase::class.java,
                        name = "note_harmony_database"
                    ).build()
                    val collectionDao = noteHarmonyDatabase.collectionDao()
                    collectionDao.deleteAll()
                    collections.clear()
                    withContext(Dispatchers.Main) {
                        collectionsAdapter.notifyDataSetChanged()
                        toggleVisibility(collectionsAdapter.itemCount)

                    }
                }
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setIcon(R.drawable.delete_24px)
            .show()
    }

    fun toggleVisibility(itemCount: Int) {
        val noCollectionsInfoTextView = binding.noCollectionsInfo

        if (itemCount != 0) {
            noCollectionsInfoTextView.visibility = View.GONE
            collectionsRecyclerView.visibility = View.VISIBLE
        } else {
            noCollectionsInfoTextView.visibility = View.VISIBLE
            collectionsRecyclerView.visibility = View.GONE
        }
    }

    override fun onClick(position: Int) {
        val intent = Intent(this@MainActivity, NotesActivity::class.java)
        intent.putExtra("collection name", collections[position].collectionName)
        startActivity(intent)
    }
}