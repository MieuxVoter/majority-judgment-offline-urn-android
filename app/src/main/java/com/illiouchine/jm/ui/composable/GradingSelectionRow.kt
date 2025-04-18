package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.R
import com.illiouchine.jm.logic.DEFAULT_GRADING_QUALITY_VALUE
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.gradings
import com.illiouchine.jm.ui.theme.JmTheme

@Composable
fun GradingSelectionRow(
    modifier: Modifier = Modifier,
    grading: Grading = DEFAULT_GRADING_QUALITY_VALUE,
    onGradingSelected: (Grading) -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var expanded by remember { mutableStateOf(false) }

        Text(stringResource(R.string.label_grades))
        TextButton(
            onClick = { expanded = !expanded }
        ) {
            Text(stringResource(grading.name))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "drop dowm arrow"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            gradings.forEach { grading ->
                DropdownMenuItem(
                    text = { Text(stringResource(grading.name)) },
                    onClick = {
                        expanded = false
                        onGradingSelected(grading)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewGradingSelectionRow() {
    JmTheme {
        GradingSelectionRow()
    }
}
