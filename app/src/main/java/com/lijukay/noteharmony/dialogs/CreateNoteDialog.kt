package com.lijukay.noteharmony.dialogs

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lijukay.noteharmony.R
import com.lijukay.noteharmony.activity.NotesActivity
import com.lijukay.noteharmony.adapter.NotesAdapter
import com.lijukay.noteharmony.databases.Note
import com.lijukay.noteharmony.databases.NoteHarmonyDatabase
import com.lijukay.noteharmony.databinding.DialogCreateBinding
import io.noties.markwon.Markwon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class CreateNoteDialog(
    private val context: Context,
    private val notesAdapter: NotesAdapter,
    private val collectionName: String,
    private val noteTitle: String?,
    private val noteContent: String?
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

        if (noteTitle != null && noteContent != null) {
            tONEditText.text = Editable.Factory.getInstance().newEditable(noteTitle)
            contentEditText.text = Editable.Factory.getInstance().newEditable(noteContent)
        }

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
                val noteHarmonyDatabase = Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = NoteHarmonyDatabase::class.java,
                    name = "note_harmony_database"
                ).build()

                val noteDao = noteHarmonyDatabase.noteDao()
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