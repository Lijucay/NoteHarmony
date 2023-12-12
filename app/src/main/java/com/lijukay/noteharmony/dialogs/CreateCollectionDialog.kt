package com.lijukay.noteharmony.dialogs

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lijukay.noteharmony.R
import com.lijukay.noteharmony.activity.MainActivity
import com.lijukay.noteharmony.adapter.CollectionsAdapter
import com.lijukay.noteharmony.databases.Collection
import com.lijukay.noteharmony.databases.NoteHarmonyDatabase
import com.lijukay.noteharmony.databinding.DialogCreateBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * This class represents a bottom sheet dialog for creating a new collection item
 * @see Collection
 * @param context The activity context
 * @param adapter The adapter for the collection item recyclerview
 */
class CreateCollectionDialog(
    private val context: Context,
    private val adapter: CollectionsAdapter
) : BottomSheetDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DialogCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val titleTextView = binding.titleTextView
        val addButton = binding.addButton
        val cancelButton = binding.cancelButton
        val tONEditText = binding.tonEditText
        val contentEditText = binding.contentEditText

        titleTextView.text = context.getString(R.string.new_collection)
        tONEditText.hint = context.getString(R.string.collection_title)
        contentEditText.hint = context.getString(R.string.collection_description)

        addButton.setOnClickListener {
            val tONString = tONEditText.text.toString().trim()
            val contentString = contentEditText.text.toString().trim()

            if (tONString.isEmpty() || contentString.isEmpty()) {
                Toast.makeText(context, context.getString(R.string.title_and_description_required), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val collection = Collection(collectionName = tONString, collectionDescription = contentString)
            val collectionListSize = adapter.itemCount
            lifecycleScope.launch(Dispatchers.IO) {
                val noteHarmonyDatabase = Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = NoteHarmonyDatabase::class.java,
                    name = "note_harmony_database"
                ).build()

                val collectionDao = noteHarmonyDatabase.collectionDao()
                val result = collectionDao.insertCollection(collection = collection)

                if (result != -1L) {
                    adapter.addCollection(collection = collection)
                    withContext(context = Dispatchers.Main) {
                        adapter.notifyItemInserted(collectionListSize)
                        (context as? MainActivity)?.toggleVisibility(adapter.itemCount)
                        dismiss()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, context.getString(R.string.collection_with_same_name_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        cancelButton.setOnClickListener {
            dismiss()
        }

        setCancelable(false)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.root.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
}