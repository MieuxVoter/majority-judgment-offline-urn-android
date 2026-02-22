package com.illiouchine.jm.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.illiouchine.jm.R
import com.illiouchine.jm.logic.HomeViewModel
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.composable.PollDeletionConfirmationDialog
import com.illiouchine.jm.ui.composable.PollSummary
import com.illiouchine.jm.ui.composable.scaffold.MjuScaffold
import com.illiouchine.jm.ui.navigator.Screens
import com.illiouchine.jm.ui.preview.PreviewDataBuilder
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewState: HomeViewModel.HomeViewState = HomeViewModel.HomeViewState(),
    onBottomBarItemSelected: (item: NavKey) -> Unit = {},
    onSetupBlankPoll: () -> Unit = {},
    onSetupTemplatePoll: (templateSlug: String) -> Unit = {},
    onSetupClonePoll: (poll: Poll) -> Unit = {},
    onResumePoll: (poll: Poll) -> Unit = {},
    onShowResult: (poll: Poll) -> Unit = {},
    onDeletePoll: (poll: Poll) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()

    MjuScaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        showMenu = true,
        menuItemSelected = Screens.Home,
        onMenuItemSelected = { destination ->
            coroutineScope.launch {
                onBottomBarItemSelected(destination)
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(Theme.spacing.medium)
                    .testTag("home_fab"),
                onClick = { onSetupBlankPoll() },
                icon = { Icon(Icons.Filled.Add, null) },
                text = { Text(stringResource(R.string.button_new_poll)) },
            )
        }
    ) { innerPadding ->

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = Theme.spacing.medium)
                .verticalScroll(state = scrollState)
        ) {
            val titleContentDescription = (
                stringResource(R.string.majority_judgment) +
                    " " +
                    stringResource(R.string.menu_home)
                )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Theme.spacing.medium)
                    .semantics {
                        contentDescription = titleContentDescription
                    },
                fontSize = 32.sp,
                lineHeight = 32.sp,
                textAlign = TextAlign.Center,
                text = stringResource(R.string.title_multiline_majority_judgment_urn),
            )

            if (homeViewState.polls.isEmpty()) {
                Spacer(Modifier.size(Theme.spacing.large))
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.incitation_making_new_poll),
                    fontStyle = FontStyle.Italic,
                )
            }

            // Not sure how to access the FAB with TalkBack
            Spacer(Modifier.height(Theme.spacing.medium))
            Button(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                onClick = { onSetupBlankPoll() },
            ) {
                Icon(Icons.Filled.Add, null)
                Text(stringResource(R.string.button_new_poll))
            }

            Spacer(Modifier.height(Theme.spacing.medium))

            homeViewState.polls.reversed().forEach { poll ->

                val showDeletionDialog = remember { mutableStateOf(false) }

                PollSummary(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Theme.spacing.small),
                    poll = poll,
                    onSetupClonePoll = { onSetupClonePoll(it) },
                    onResumePoll = { onResumePoll(it) },
                    onShowResult = { onShowResult(it) },
                    onDeletePoll = {
                        showDeletionDialog.value = true
                    },
                )
                Spacer(
                    Modifier
                        .padding(bottom = 48.dp)
                        .height(1.dp)
                        .fillMaxWidth()
                        .background(Color.LightGray),
                )

                if (showDeletionDialog.value) {
                    PollDeletionConfirmationDialog(
                        poll = poll,
                        onConfirm = {
                            showDeletionDialog.value = false
                            onDeletePoll(poll)
                        },
                        onDismiss = {
                            showDeletionDialog.value = false
                        },
                    )
                }
            }

            Spacer(Modifier.height(Theme.spacing.medium))

            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = stringResource(R.string.home_try_poll_templates)
            )
            homeViewState.templates.forEach { template ->
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                    onClick = {
                        onSetupTemplatePoll(template.slug)
                    },
                ) {
                    Text(template.config.subject)
                }
                Spacer(Modifier.height(Theme.spacing.extraSmall))
            }

            // Leave room for the floating action button
            Spacer(Modifier.height(Theme.spacing.large * 3))
        }
    }
}

@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=511dp,height=891dp,orientation=landscape",
)
@Composable
fun PreviewHomeScreen(modifier: Modifier = Modifier) {
    JmTheme {
        HomeScreen(
            modifier = modifier,
            homeViewState = HomeViewModel.HomeViewState(
                polls = listOf(
                    PreviewDataBuilder.poll(0, 3),
                    PreviewDataBuilder.poll(1, 0),
                    PreviewDataBuilder.poll(1, 2)
                )
            )
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewHomeScreenWithEmptyPoll(modifier: Modifier = Modifier) {
    JmTheme {
        HomeScreen(
            modifier = modifier,
            homeViewState = HomeViewModel.HomeViewState(
                polls = emptyList(),
            )
        )
    }
}
