package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


@Composable
fun IconTextButton(
    modifier: Modifier = Modifier,
    icon: ImageVector?,
    text: String,
    onClick: () -> Unit,
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        if (null != icon) {
            Icon(
                imageVector = icon,
                contentDescription = "", // aria-ignore (cosmetic)
                modifier = Modifier.padding(end = 8.dp),
            )
        }
        Text(
            text = text,
        )
    }
}
