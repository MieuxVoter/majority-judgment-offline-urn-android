package com.illiouchine.jm.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.ui.theme.JmTheme
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

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
@Composable
private fun DarkThemedHorizontalDivider() {
    JmTheme {
        Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
            ThemedHorizontalDivider()
        }
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    showBackground = true
)
@Composable
private fun LightThemedHorizontalDivider() {
    JmTheme {
        Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
            ThemedHorizontalDivider()
        }
    }
}
