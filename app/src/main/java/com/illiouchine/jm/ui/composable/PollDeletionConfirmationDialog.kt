package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Dialog
import com.illiouchine.jm.R
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.theme.DeleteColor

@Composable
fun PollDeletionConfirmationDialog(
    modifier: Modifier = Modifier,
    poll: Poll,
    onConfirm: (poll: Poll) -> Unit = {},
    onDismiss: (poll: Poll) -> Unit = {},
) {
    Dialog(
        onDismissRequest = { onDismiss(poll) },
    ) {
        Card(
            modifier = modifier,
        ) {
            Column(
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                ),
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontSize = 5.em,
                    fontWeight = FontWeight.Bold,
                    text = stringResource(R.string.dialog_are_you_sure),
                )
                Text(text = stringResource(R.string.dialog_this_will_delete_the_poll))
                Text(
                    fontStyle = FontStyle.Italic,
                    text = poll.pollConfig.subject,
                )
                Text(
                    modifier = Modifier.padding(top = 16.dp),
                    fontSize = 3.em,
                    text = stringResource(R.string.dialog_operation_cannot_be_undone),
                )
                Row {
                    TextButton(
                        onClick = { onDismiss(poll) },
                    ) {
                        Text(stringResource(R.string.action_cancel))
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(
                        onClick = { onConfirm(poll) },
                        colors = ButtonDefaults.textButtonColors().copy(
                            contentColor = DeleteColor,
                        ),
                    ) {
                        Text(stringResource(R.string.action_delete))
                    }
                }
            }
        }
    }
}
