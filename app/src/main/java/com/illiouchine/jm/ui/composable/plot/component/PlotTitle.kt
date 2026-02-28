package com.illiouchine.jm.ui.composable.plot.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing

@Composable
fun PlotTitle(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth().padding(
            horizontal = Theme.spacing.medium,
        ),
        lineHeight = 10.sp,
        fontSize = 10.sp,
        fontStyle = FontStyle.Italic,
        textAlign = TextAlign.Center,
    )
}