package com.illiouchine.jm.ui.composable.title

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SectionTitleWithIcon(
    text: String,
    iconImage: ImageVector = Icons.Default.Info,
    iconDescription: String? = null,
    onClickOnIcon: () -> Unit = {},
) {
    Row {
        SectionTitle(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = text,
        )
        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = onClickOnIcon,
        ) {
            Icon(
                imageVector = iconImage,
                contentDescription = iconDescription,
            )
        }
    }
}
