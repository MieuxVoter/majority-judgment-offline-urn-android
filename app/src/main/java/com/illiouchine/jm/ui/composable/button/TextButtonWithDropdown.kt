package com.illiouchine.jm.ui.composable.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList

@Composable
fun TextButtonWithDropdown(
    modifier: Modifier = Modifier,
    currentValueIndex: Int,
    values: PersistentList<String>,
    onClickLabel: String? = null,
    onChange: (newIndex: Int) -> Unit = {},
) {
    if (values.isEmpty()) {
        return
    }

    // Experimenting with padding values for the button, but not sure if this is relevant.
    val buttonPadding = PaddingValues(
        start = 3.dp,
        top = 0.dp,
        end = 3.dp,
        bottom = 0.dp,
    )
    // The default minimum values are a little big for our needs, so let's use lower ones.
    val buttonMinHeight = 28.dp
    val buttonMinWidth = 28.dp

    var dropdownExpanded by remember { mutableStateOf(false) }

    TextButton(
        modifier = modifier
            // .align(Alignment.CenterVertically)
            .heightIn(min = buttonMinHeight)
            .widthIn(min = buttonMinWidth)
            .semantics {
                onClick(
                    label = onClickLabel,
                    action = null,
                )
            },
        shape = MaterialTheme.shapes.small,
        contentPadding = buttonPadding,
        onClick = {
            dropdownExpanded = !dropdownExpanded
        },
    ) {
        Text(values[currentValueIndex])
    }
    DropdownMenu(
        expanded = dropdownExpanded,
        onDismissRequest = {
            dropdownExpanded = false
        },
    ) {
        for ((itemIndex, itemValue) in values.withIndex()) {
            DropdownMenuItem(
                modifier = Modifier
                    .semantics {
                        // contentDescription = "to do ; how exactly is this used, again ?"
                    },
                enabled = true,
                text = {
                    Text(
                        text = itemValue,
                    )
                },
                onClick = {
                    dropdownExpanded = false
                    if (itemIndex != currentValueIndex) {
                        onChange(itemIndex)
                    }
                },
            )
        }
    }
}
