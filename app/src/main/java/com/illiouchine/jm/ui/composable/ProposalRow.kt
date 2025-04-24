package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.illiouchine.jm.ui.theme.DeleteColor
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing

@Composable
fun ProposalRow(
    modifier: Modifier = Modifier,
    proposal: String,
    onRemoveClicked: (String) -> Unit = {},
) {
    Row(
        modifier = modifier.padding(vertical = Theme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f),
            text = proposal,
        )
        Spacer(
            Modifier
                .height(Theme.spacing.medium)
                .padding(Theme.spacing.small)
        )
        IconButton(
            onClick = { onRemoveClicked(proposal) }
        ) {
            Icon(
                modifier = Modifier,
                imageVector = Icons.Outlined.Delete,
                contentDescription = "",
                tint = DeleteColor,
            )
        }
    }
}
