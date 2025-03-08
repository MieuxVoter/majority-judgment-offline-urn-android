package com.illiouchine.jm.ui.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.R
import com.illiouchine.jm.ui.theme.JmTheme

@Composable
fun ColumnScope.ProposalSelectionRow(
    modifier: Modifier = Modifier,
    proposal: String = "",
    onProposalChange: (String) -> Unit = {},
    onAddProposal: (String) -> Unit = {},
    proposalSuggestion: List<String> = emptyList(),
    onProposalSelected: (String) -> Unit = {},
    onClearSuggestion: () -> Unit = {},
) {
    var textFieldHeight by remember { mutableIntStateOf(0) }
    // Helps us put the cursor at the end after selecting a suggestion.
    var textFieldSelection by remember {
        mutableStateOf(
            TextRange(proposal.length)
        )
    }

    Row {
        TextField(
            value = TextFieldValue(
                text = proposal,
                selection = textFieldSelection,
            ),
            label = { Text(stringResource(R.string.label_poll_proposals)) },
            onValueChange = {
                textFieldSelection = it.selection
                onProposalChange(it.text)
            },
            modifier = modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldHeight = coordinates.size.height
                }
                .onFocusChanged {
                    if (!it.isFocused) {
                        onClearSuggestion()
                    }
                },
            singleLine = true,
            placeholder = { Text("Entrez une proposition…") },
            keyboardActions = KeyboardActions(
                onDone =
                if (proposal.isBlank()) {
                    // A null value indicates that the default implementation should be executed
                    // This helps older Android version users to close the keyboard
                    null
                } else {
                    {
                        onAddProposal(proposal)
                    }
                }
            ),
            trailingIcon = {
                IconButton(
                    modifier = Modifier.testTag("setup_add_proposal"),
                    onClick = {
                        onAddProposal(proposal)
                    },
                ) {
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.Outlined.Add,
                        contentDescription = stringResource(R.string.button_add),
                    )
                }
            }
        )
        AnimatedVisibility(
            visible = proposalSuggestion.isNotEmpty()
        ) {
            SuggestionPopup(
                offset = IntOffset(0, textFieldHeight),
                modifier = Modifier,
                suggestions = proposalSuggestion.take(3),
                onSuggestionSelected = {
                    textFieldSelection = TextRange(it.length)
                    onProposalSelected(it)
                },
                onClearSuggestion = { onClearSuggestion() },
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewProposalSelectionRow() {
    JmTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
                .padding(16.dp)
        ) {
            ProposalSelectionRow(
                modifier = Modifier,
                proposal = "proposal",
                onAddProposal = {},
                onProposalChange = {},
                proposalSuggestion = listOf(
                    "Subject suggestion 1",
                    "Subject suggestion 2",
                    "Subject suggestion 3",
                    "Subject suggestion 4",
                )
            )
        }
    }
}