package com.illiouchine.jm.ui.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.illiouchine.jm.R
import com.illiouchine.jm.ui.theme.JmTheme

@Composable
fun ColumnScope.SubjectSelectionRow(
    modifier: Modifier = Modifier,
    subject: String = "",
    subjectSuggestion: List<String> = emptyList(),
    onSubjectChange: (String) -> Unit = {},
    onSuggestionSelected: (String) -> Unit = {},
) {
    var textFieldHeight by remember { mutableIntStateOf(0) }
    Row {
        TextField(
            label = { Text(stringResource(R.string.label_poll_subject)) },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldHeight = coordinates.size.height

                },
            maxLines = 5,
            placeholder = { Text("Entrez le sujet du scrutin...") },
            value = subject,
            onValueChange = { onSubjectChange(it) },
        )
        AnimatedVisibility(
            visible = subjectSuggestion.isNotEmpty()
        ) {
            Popup(
                offset = IntOffset(0, textFieldHeight),
                alignment = Alignment.TopStart,
                onDismissRequest = {},
            ) {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    userScrollEnabled = false,
                ) {
                    itemsIndexed(
                        items = subjectSuggestion.take(3)
                    ) { index, suggestion ->
                        if (index > 0) {
                            ThemedHorizontalDivider()
                        }
                        TextButton(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .background(Color.Red),
                            onClick = { onSuggestionSelected(suggestion) }
                        ) {
                            Text(suggestion)
                        }
                    }
                }
            }
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
                onSuggestionSelected = {},
                onSubjectChange = {},
            )
            Text("Should be under suggestion")
        }
    }
}