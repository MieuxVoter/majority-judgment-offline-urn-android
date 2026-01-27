package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing

@Composable
fun PollSubject(
    modifier: Modifier = Modifier,
    subject: String = "",
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.colorScheme.primary)
            .padding(
                start = Theme.spacing.small,
                end = Theme.spacing.small,
                top = Theme.spacing.medium + Theme.spacing.small,
            ),
    ) {
        Text(
            modifier = modifier,
            text = subject,
            color = Theme.colorScheme.onPrimary,
            fontSize = 24.sp,
        )
    }
}
