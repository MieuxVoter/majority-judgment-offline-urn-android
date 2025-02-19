package com.illiouchine.jm.ui.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.ui.theme.JmTheme
import kotlinx.coroutines.delay

@Composable
fun MUSnackbar(
    modifier: Modifier = Modifier,
    text: String? = "",
    onDismiss: () -> Unit = {},
) {
    if (!text.isNullOrEmpty()) {
        LaunchedEffect(text) {
            delay(5 * 1000)
            onDismiss()
        }
        Snackbar(
            modifier = modifier
                .padding(16.dp)
                .padding(WindowInsets.ime.asPaddingValues()),
            dismissAction = {
                Button(
                    modifier = Modifier.padding(8.dp),
                    onClick = { onDismiss() },
                ) {
                    Text("Dismiss")
                }
            },
        ) {
            Text(text)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PreviewSnackBar() {
    JmTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                MUSnackbar(
                    modifier = Modifier,
                    text = "J'aime les snackbars !"
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {}
        }
    }
}