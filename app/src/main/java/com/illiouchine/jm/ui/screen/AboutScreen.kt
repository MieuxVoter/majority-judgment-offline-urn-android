package com.illiouchine.jm.ui.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.illiouchine.jm.Screens
import com.illiouchine.jm.ui.composable.MjuBottomBar
import com.illiouchine.jm.ui.theme.JmTheme


@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    onBrowseSource: () -> Unit = {},
    onReportBug: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            MjuBottomBar(
                selected = navController.currentDestination?.route ?: Screens.About.name,
                onItemSelected = { destination -> navController.navigate(destination.id) },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.padding(8.dp),
                onClick = { onReportBug() },
                icon = { Icon(Icons.Filled.Build, "Report") },
                text = { Text(text = "Report a Bug") },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(state = ScrollState(initial = 0))
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 64.dp),
                fontSize = 8.em,
                textAlign = TextAlign.Center,
                lineHeight = 1.3.em,
                text = "About",
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "This app is libre software.",
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Help us make it better !",
            )

            Spacer(Modifier.padding(16.dp))

            OutlinedButton(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                onClick = { onBrowseSource() },
            ) {
                Text("Browse the Source")
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewAboutScreen(modifier: Modifier = Modifier) {
    JmTheme {
        AboutScreen()
    }
}
