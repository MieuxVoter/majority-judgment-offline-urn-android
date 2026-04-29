package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing

@Composable
fun BallotsFilter(
    onFilterDelete: () -> Unit,
//    onFilterUpdate: (BallotsFilterInterface) -> Unit,
    content: @Composable (
    FlowRowScope.(
//        onFilterUpdate: (BallotsFilterInterface) -> Unit,
    ) -> Unit
    ),
) {
    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Theme.colorScheme.primary,
            )
            .padding(
                start = Theme.spacing.small,
            )
    ) {
        FlowRow(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            content = {
//                content(onFilterUpdate)
                content()
            },
        )
        Row(
            modifier = Modifier
                .align(Alignment.CenterVertically),
        ) {
            IconButton(
                onClick = onFilterDelete,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove this filter.",
                )
            }
        }
    }
}
