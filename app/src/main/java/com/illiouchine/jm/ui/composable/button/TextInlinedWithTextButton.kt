package com.illiouchine.jm.ui.composable.button

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun RowScope.TextInlinedWithTextButton(
    text: String,
    modifier: Modifier = Modifier,
) {
    // Adapt the raw text inlined with buttons to the size of the text in the buttons.
    // This value was found empirically, and may not work in some layouts.  Time will tell.
    val fontSizeTextButton = 14.sp

    Text(
        modifier = modifier
            .align(Alignment.CenterVertically),
        text = text,
        fontSize = fontSizeTextButton,
    )
}
