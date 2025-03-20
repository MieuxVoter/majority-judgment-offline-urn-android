package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.model.Poll

@Composable
fun PollRow(
    modifier: Modifier = Modifier,
    poll:Poll,
    onSetupClonePoll: (poll: Poll) -> Unit,
    onResumePoll: (poll: Poll) -> Unit,
    onShowResult: (poll: Poll) -> Unit,
    onDeletePoll: (poll: Poll) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {

        val showDeletionDialog = remember { mutableStateOf(false) }
        PollSummary(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            poll = poll,
            onSetupClonePoll = { onSetupClonePoll(it) },
            onResumePoll = { onResumePoll(it) },
            onShowResult = { onShowResult(it) },
            onDeletePoll = {
                showDeletionDialog.value = true
            },
        )
        Spacer(
            Modifier
                .height(1.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Color.LightGray),
        )

        if (showDeletionDialog.value) {
            PollDeletionConfirmationDialog(
                poll = poll,
                onConfirm = {
                    showDeletionDialog.value = false
                    onDeletePoll(poll)
                },
                onDismiss = {
                    showDeletionDialog.value = false
                },
            )
        }
    }
}