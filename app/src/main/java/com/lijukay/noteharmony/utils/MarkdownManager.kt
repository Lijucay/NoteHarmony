package com.lijukay.noteharmony.utils

import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan

class MarkdownManager {

    fun toMarkdown(text: String): SpannableString {
        val regex = Regex("\\*(.*?)\\*")

        val spannableString = SpannableString(text)
        val matches = regex.findAll(text)

        matches.forEach {
            val start = it.range.first + 1
            val end = it.range.last


            spannableString.setSpan(
                spannableString.replaceFirst(Regex("\\*"), "<i>").replaceFirst(Regex("\\*"), "</i>"),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannableString

    }

}