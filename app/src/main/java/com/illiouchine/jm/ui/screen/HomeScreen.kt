package com.illiouchine.jm.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.illiouchine.jm.ui.theme.JmTheme


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier, // TODO: why is this applied on the Box and not Scaffold ?  (purpose?)
    onSetupBlankPoll: () -> Unit = {},
//    onSetupClonePoll: (poll: Poll) -> Unit = {}, // probably
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->

        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Row(modifier = Modifier.align(Alignment.BottomEnd)) {
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(24.dp),
                    onClick = { onSetupBlankPoll() },
                    icon = { Icon(Icons.Filled.Add, "Extended floating action button.") },
                    text = { Text(text = "New Poll") },
                )
            }
        }

        Column(
            modifier = Modifier.padding(top=64.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(bottom=64.dp),
                fontSize = 8.em,
                textAlign = TextAlign.Center,
                lineHeight = 1.3.em,
                text = "Majority\nJudgment\nUrn",
            )

            Text(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                textAlign = TextAlign.Center,
                text = "Try making a new poll, it's free !",
                fontStyle = FontStyle.Italic,
            )

        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewHomeScreen(modifier: Modifier = Modifier) {
    JmTheme { HomeScreen() }
}