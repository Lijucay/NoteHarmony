package com.lijukay.yana

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lijukay.yana.adapter.CollectionsAdapter
import com.lijukay.yana.databases.CollectionDatabase
import com.lijukay.yana.databinding.ActivityMainBinding
import com.lijukay.yana.databinding.DialogHiddenNotesBinding
import com.lijukay.yana.items.Collection

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val notesCollectionRecyclerView = binding.noteCollectionRecyclerView
        val materialToolbar = binding.materialToolbar
        val bottomAppBar = binding.bottomAppBar

        setSupportActionBar(materialToolbar)

        bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.invisible_menu_item -> {
                    showVerificationDialog {
                        showHiddenNotesDialog()
                    }
                }
            }
            return@setOnMenuItemClickListener false
        }

        val collections = CollectionDatabase(this).getAllCollections()
        val collectionsAdapter = CollectionsAdapter(collections)
        notesCollectionRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        notesCollectionRecyclerView.adapter = collectionsAdapter
    }

    private fun showHiddenNotesDialog() {
        val dialog = BottomSheetDialog(this@MainActivity)
        val dialogBinding = DialogHiddenNotesBinding.inflate(layoutInflater)
        val hiddenNotesRecyclerView = dialogBinding.hiddenNotesRecyclerView



        dialog.setContentView(dialogBinding.root)
        val bottomSheetBehavior = BottomSheetBehavior.from(dialogBinding.root.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.show()
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
}