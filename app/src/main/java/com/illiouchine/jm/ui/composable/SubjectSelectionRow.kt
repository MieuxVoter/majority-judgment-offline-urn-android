package com.illiouchine.jm.ui.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.R
import com.illiouchine.jm.ui.theme.JmTheme

@Composable
@Suppress("UnusedReceiverParameter")
fun ColumnScope.SubjectSelectionRow(
    modifier: Modifier = Modifier,
    subject: String = "",
    subjectSuggestion: List<String> = emptyList(),
    onSubjectChange: (String) -> Unit = {},
    onSuggestionSelected: (String) -> Unit = {},
    onClearSuggestion: () -> Unit = {},
) {
    var textFieldHeight by remember { mutableIntStateOf(0) }
    Row {
        TextField(
            label = { Text(stringResource(R.string.label_poll_subject)) },
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
            placeholder = {
                Text(stringResource(R.string.poll_setup_subject_placeholder))
            },
            value = subject,
            onValueChange = { onSubjectChange(it) },
        )
        AnimatedVisibility(
            visible = subjectSuggestion.isNotEmpty()
        ) {
            SuggestionPopup(
                offset = IntOffset(0, textFieldHeight),
                modifier = Modifier,
                suggestions = subjectSuggestion.take(3),
                onSuggestionSelected = { onSuggestionSelected(it) },
                onClearSuggestion = { onClearSuggestion() }
            )
        }
    }
}


@Preview(showSystemUi = true)
@Composable
private fun PreviewSelectionRow() {
    JmTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
                .padding(16.dp)
        ) {
            SubjectSelectionRow(
                modifier = Modifier,
                subject = "my dummy subject",
                subjectSuggestion = listOf(
                    "Subject suggestion 1",
                    "Subject suggestion 2",
                    "Subject suggestion 3",
                    "Subject suggestion 4",
                ),
            )
            Text("Should be under suggestion")
        }
    }
}