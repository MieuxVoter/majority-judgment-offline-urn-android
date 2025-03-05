package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.illiouchine.jm.ui.theme.SeparatorDark
import com.illiouchine.jm.ui.theme.SeparatorLight

@Composable
fun ThemedHorizontalDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        color = if (isSystemInDarkTheme()) SeparatorDark else SeparatorLight,
        modifier = modifier.fillMaxWidth(),
    )
}