package com.illiouchine.jm.ui.composable.scaffold

import androidx.annotation.StringRes
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.illiouchine.jm.R
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
import kotlinx.coroutines.delay

@Composable
fun MjuSnackbar(
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
                .padding(Theme.spacing.medium)
                .padding(WindowInsets.ime.asPaddingValues()),
            dismissAction = {
                Button(
                    modifier = Modifier.padding(Theme.spacing.small),
                    onClick = { onDismiss() },
                ) {
                    Text(stringResource(R.string.action_dismiss))
                }
            },
        ) {
            Text(text)
        }
    }
}

@Composable
fun MjuSnackbarWithStringResId(
    modifier: Modifier = Modifier,
    @StringRes textId: Int? = null,
    onDismiss: () -> Unit = {},
) {
    textId?.let {
        LaunchedEffect(it) {
            delay(5 * 1000)
            onDismiss()
        }
        Snackbar(
            modifier = modifier
                .padding(Theme.spacing.medium)
                .padding(WindowInsets.ime.asPaddingValues()),
            dismissAction = {
                Button(
                    modifier = Modifier.padding(Theme.spacing.small),
                    onClick = { onDismiss() },
                ) {
                    Text(stringResource(R.string.action_dismiss))
                }
            },
        ) {
            Text(stringResource(textId))
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
                MjuSnackbar(
                    modifier = Modifier,
                    text = "I love snackbars !"
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {}
        }
    }
}
