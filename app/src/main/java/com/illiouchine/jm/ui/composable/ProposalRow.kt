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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import com.illiouchine.jm.R
import com.illiouchine.jm.ui.theme.DeleteColor
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing

@Composable
fun ProposalRow(
    modifier: Modifier = Modifier,
    proposal: String,
    onRemoveClicked: (String) -> Unit = {},
) {
    val context = LocalContext.current

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
        val removeText = context.getString(R.string.tts_remove_the_proposal) + " " + proposal
        IconButton(
            modifier = Modifier
                .semantics(mergeDescendants = true) {
                    onClick(
                        label = removeText,
                        action = null,
                    )
                },
            onClick = { onRemoveClicked(proposal) },
        ) {
            Icon(
                modifier = Modifier,
                imageVector = Icons.Outlined.Delete,
                contentDescription = removeText,
                tint = DeleteColor,
            )
        }
    }
}
