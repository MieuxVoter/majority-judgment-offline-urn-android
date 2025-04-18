package com.illiouchine.jm.service

import android.text.Html
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml

class TextStylist {
    fun annotateGradeName(gradeName: String): String {
        return "<em>${escape(gradeName)}</em>"
    }

    fun resolve(s: String): AnnotatedString {
        return AnnotatedString.fromHtml(htmlString = s)
    }

    private fun escape(s: String): String {
        return Html.escapeHtml(s)
    }
}
