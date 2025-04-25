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
import androidx.compose.ui.window.Popup
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing

@Composable
fun SuggestionPopup(
    modifier: Modifier = Modifier,
    offset: IntOffset = IntOffset(0, 0),
    suggestions: List<String> = emptyList(),
    onSuggestionSelected: (String) -> Unit = {},
    onClearSuggestion: () -> Unit = {},
) {
    Popup(
        offset = offset,
        alignment = Alignment.TopStart,
        onDismissRequest = { onClearSuggestion() },
    ) {
        LazyColumn(
            modifier = modifier
                .padding(horizontal = Theme.spacing.medium)
                .background(MaterialTheme.colorScheme.primary),
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
                        .padding(Theme.spacing.medium)
                        .clickable {
                            onSuggestionSelected(suggestion)
                        },
                    text = suggestion,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}
