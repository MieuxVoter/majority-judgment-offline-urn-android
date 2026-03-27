package com.illiouchine.jm.ui.composable.spacer

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing

@Composable
fun MediumVerticalSpacer(
    modifier: Modifier = Modifier,
) {
    Spacer(
        modifier = modifier.padding(
            vertical = Theme.spacing.medium,
        ),
    )
}
