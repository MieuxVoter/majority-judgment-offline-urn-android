package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing


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
                contentDescription = null, // aria-ignore (icon is cosmetic)
                modifier = Modifier.padding(end = Theme.spacing.small),
            )
        }
        Text(
            text = text,
        )
    }
}
