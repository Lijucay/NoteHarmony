package com.lijukay.noteharmony.dialogs

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog

class FolderDialog {

    companion object {
        fun create(context: Context, callback: (String) -> Unit): BottomSheetDialog {
            val dialog = BottomSheetDialog(context)


            return dialog
        }
    }

}