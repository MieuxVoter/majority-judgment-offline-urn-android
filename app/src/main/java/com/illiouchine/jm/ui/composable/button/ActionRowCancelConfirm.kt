package com.illiouchine.jm.ui.composable.button

import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.illiouchine.jm.R

@Composable
fun ActionRowCancelConfirm(
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    FlowRow {
        TextButton(
            onClick = onCancel,
        ) {
            Text(
                text = stringResource(R.string.action_cancel),
            )
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onConfirm,
        ) {
            Text(
                text = stringResource(R.string.action_confirm),
            )
        }
    }
}
