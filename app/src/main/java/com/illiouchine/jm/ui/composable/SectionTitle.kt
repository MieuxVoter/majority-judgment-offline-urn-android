package com.illiouchine.jm.ui.composable

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.illiouchine.jm.ui.theme.Theme

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = Theme.typography.titleLarge.fontSize,
    )
}
