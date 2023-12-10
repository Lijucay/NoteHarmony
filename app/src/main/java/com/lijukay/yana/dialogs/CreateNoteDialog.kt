package com.lijukay.yana.dialogs

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lijukay.yana.R
import com.lijukay.yana.activity.NotesActivity
import com.lijukay.yana.adapter.NotesAdapter
import com.lijukay.yana.databases.Note
import com.lijukay.yana.databases.YANADatabase
import com.lijukay.yana.databinding.DialogCreateBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class CreateNoteDialog(
    private val context: Context,
    private val notesAdapter: NotesAdapter,
    private val collectionName: String
): BottomSheetDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DialogCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val titleTextView = binding.titleTextView
        val addButton = binding.addButton
        val cancelButton = binding.cancelButton
        val tONEditText = binding.tonEditText
        val contentEditText = binding.contentEditText

        titleTextView.text = context.getString(R.string.new_note)
        tONEditText.hint = context.getString(R.string.note_title)
        contentEditText.hint = context.getString(R.string.note_content)

        addButton.setOnClickListener {
            val tONString = tONEditText.text.toString().trim()
            val contentString = contentEditText.text.toString().trim()

            if (tONString.isEmpty() || contentString.isEmpty()) {
                Toast.makeText(context, context.getString(R.string.title_and_content_required), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val note = Note(
                noteName = tONString,
                noteContent = contentString,
                collectionKey = collectionName,
                creationDate = Calendar.getInstance().toString(),
                isHidden = 0
            )
            val noteListSize = notesAdapter.itemCount

            lifecycleScope.launch(Dispatchers.IO) {
                val yanaDatabase = Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = YANADatabase::class.java,
                    name = "yana_database"
                ).build()

                val noteDao = yanaDatabase.noteDao()
                val result = noteDao.insertNote(note)

                if (result != -1L) {
                    notesAdapter.addNote(note = note)
                    withContext(Dispatchers.Main) {
                        notesAdapter.notifyItemInserted(noteListSize)
                        (context as? NotesActivity)?.toggleVisibility(notesAdapter.itemCount)
                        dismiss()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.note_with_same_name_error),
                            Toast.LENGTH_SHORT
                        ).show()
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