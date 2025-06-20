package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.sp

@Composable
fun PollSubject(
    modifier: Modifier = Modifier,
    subject: String = "",
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                traversalIndex = -2f
            },
    ) {
        Text(
            modifier = modifier
                .semantics {
                    traversalIndex = -1f
                },
            text = "❝ $subject ❞",
            fontSize = 24.sp,
        )
    }
}
