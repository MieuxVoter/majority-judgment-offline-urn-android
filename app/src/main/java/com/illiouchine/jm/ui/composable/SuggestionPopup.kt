package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun SuggestionPopup(
    modifier: Modifier = Modifier,
    offset: IntOffset = IntOffset(0, 0),
    suggestions: List<String> = emptyList(),
    onSuggestionSelected: (String) -> Unit = {},
) {
    Popup(
        offset = offset,
        alignment = Alignment.TopStart,
        onDismissRequest = {},
    ) {
        LazyColumn(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.onPrimaryContainer),
            horizontalAlignment = Alignment.CenterHorizontally,
            userScrollEnabled = false,
        ) {
            itemsIndexed(
                items = suggestions
            ) { index, suggestion ->
                if (index > 0) {
                    ThemedHorizontalDivider()
                }
                Text(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .clickable { onSuggestionSelected(suggestion) }
                        .padding(16.dp),
                    text = suggestion
                )
            }
        }
    }
}