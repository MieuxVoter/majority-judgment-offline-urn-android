package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.illiouchine.jm.model.PollConfig

@Composable
fun PollSubject(
    modifier: Modifier = Modifier,
    pollConfig: PollConfig,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            modifier = modifier.padding(24.dp),
            text = "❝ ${pollConfig.subject} ❞",
            fontSize = 6.em,
        )
    }
}
