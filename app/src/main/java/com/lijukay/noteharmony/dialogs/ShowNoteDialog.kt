package com.lijukay.noteharmony.dialogs

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.lijukay.noteharmony.R
import com.lijukay.noteharmony.activity.NotesActivity
import com.lijukay.noteharmony.databases.Note
import com.lijukay.noteharmony.databases.NoteHarmonyDatabase
import com.lijukay.noteharmony.databinding.DialogShowBinding
import com.lijukay.noteharmony.utils.MarkdownManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class ShowNoteDialog(
    private val context: Context,
    private val noteTitle: String,
    private val noteContent: String
): BottomSheetDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DialogShowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val titleTextView = binding.titleTextView
        val updateButton = binding.updateButton
        val contentTextView = binding.contentTextView

        titleTextView.text = noteTitle
        contentTextView.text = Html.fromHtml(formatString(noteContent), Html.FROM_HTML_MODE_COMPACT)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.root.parent as View)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun formatString(text: String) : String {
        val italicBoldRegex = Regex("(_\\*{2}|\\*{2}_)(.*?)(_\\*{2}|\\*{2}_)|(_\\*{1}|\\*{1}_)(.*?)(_\\*{1}|\\*{1}_)|(_)(.*?)(_)|(\\*{2})(.*?)(\\*{2})|(\\*{1})(.*?)(\\*{1})")

        return text.replace(italicBoldRegex) { matchResult ->
            val italicBold1 = matchResult.groupValues[1]
            val italicBoldContent1 = matchResult.groupValues[2]
            val italicBold2 = matchResult.groupValues[3]
            val italic1 = matchResult.groupValues[5]
            val italicContent = matchResult.groupValues[6]
            val italic2 = matchResult.groupValues[7]
            val bold1 = matchResult.groupValues[8]
            val boldContent = matchResult.groupValues[9]
            val bold2 = matchResult.groupValues[10]
            val asterisk1 = matchResult.groupValues[11]
            val asteriskContent = matchResult.groupValues[12]
            val asterisk2 = matchResult.groupValues[13]

            when {
                italicBold1.isNotEmpty() && italicBold2.isNotEmpty() -> "<i><b>$italicBoldContent1</b></i>"
                italic1.isNotEmpty() && italic2.isNotEmpty() -> "<i>$italicContent</i>"
                bold1.isNotEmpty() && bold2.isNotEmpty() -> "<b>$boldContent</b>"
                asterisk1.isNotEmpty() && asterisk2.isNotEmpty() -> "<b>$asteriskContent</b>"
                else -> matchResult.value
            }
        }
    }
}