package com.illiouchine.jm.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.illiouchine.jm.R
import com.illiouchine.jm.data.InMemoryPollDataSource
import com.illiouchine.jm.logic.PollQrImportViewModel
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.composable.ScreenTitle
import com.illiouchine.jm.ui.composable.button.ActionRowCancelConfirm
import com.illiouchine.jm.ui.composable.scaffold.MjuScaffold
import com.illiouchine.jm.ui.composable.spacer.MediumVerticalSpacer
import com.illiouchine.jm.ui.composable.spacer.SmallVerticalSpacer
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
import com.illiouchine.jm.ui.utils.compress
import com.illiouchine.jm.ui.utils.encode
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray

@OptIn(ExperimentalSerializationApi::class)
@Composable
fun PollQrImportScreen(
    state: PollQrImportViewModel.PollQrImportViewState,
    modifier: Modifier = Modifier,
    onCancel: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    MjuScaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->

        val scrollState = rememberScrollState()

        @Composable
        fun ColumnScope.CancelAsPrimaryButton() {
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onCancel,
            ) { Text(stringResource(R.string.action_cancel)) }
        }

        @Composable
        fun TextWithUnicodeIconRow(
            unicodeIcon: String,
            text: String,
        ) {
            Row {
                Text(
                    modifier = Modifier.padding(horizontal = Theme.spacing.tiny),
                    text = unicodeIcon,
                )
                Text(
                    text = text,
                )
            }
        }

        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = Theme.spacing.small)
                .fillMaxSize()
                .verticalScroll(state = scrollState),
        ) {
            ScreenTitle(
                text = stringResource(R.string.action_import_poll),
            )

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage,
                )
                MediumVerticalSpacer()

                if (state.qrUriPath != null) {
                    Text("Here's the compressed content we failed to import:")
                    Text(
                        text = state.qrUriPath,
                        fontFamily = FontFamily.Monospace,
                    )
                    MediumVerticalSpacer()
                }

                CancelAsPrimaryButton()

                return@Column
            }

            if (state.existingPoll != null) {
                Text(
                    text = "The poll you're trying to import already exists on this device.",
                )

                MediumVerticalSpacer()

                CancelAsPrimaryButton()

                return@Column
            }

            if (state.importedPoll != null) {
                Text(
                    text = "You are about to import a poll configuration on this device. Here's what it is about:",
                )

                MediumVerticalSpacer()

                TextWithUnicodeIconRow(
                    unicodeIcon = "\uD83D\uDD2E",
                    text = state.importedPoll.pollConfig.subject,
                )

                SmallVerticalSpacer()

                for (proposalName in state.importedPoll.pollConfig.proposals) {
                    TextWithUnicodeIconRow(
                        unicodeIcon = "\uD83E\uDDD8",
                        text = proposalName,
                    )
                }

                SmallVerticalSpacer()

                TextWithUnicodeIconRow(
                    unicodeIcon = "⛅",
                    text = stringResource(state.importedPoll.pollConfig.grading.name),
                )

                MediumVerticalSpacer()

                ActionRowCancelConfirm(
                    onCancel = onCancel,
                    onConfirm = onConfirm,
                )
            }

            MediumVerticalSpacer()
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Preview(
    name = "Phone (Portrait)",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 1.0f,
)
 @Preview(
    name = "Phone (Portrait, Big Font)",
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 2.0f,
 )
 @Preview(
    name = "Tablet",
    device = "spec:width=1280dp,height=800dp,dpi=240",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = true,
 )
@Composable
fun PreviewPollQrImportScreen(modifier: Modifier = Modifier) {
    val poll = Poll(
        id = 17,
        pollConfig = PollConfig(
            subject = "Présidence Française 2027",
            proposals = listOf(
                "Jean-Luc Mélenchon",
                "Jordan Bardella",
                "Chloé Ridel",
                "Édouard Philippe",
                "François Ruffin",
                "Bruno Retailleau",
                "Philippe Poutou",
                "Gabriel Attal",
                "Fabien Roussel",
                "François Asselineau",
                "C'est Pas Marqué dans les Livres, le Plus Important À Vivr— TA GUEUUULE ! Juliette Arnaud, en Avant les Histoires !",
            ),
            grading = Grading.Quality7Grading,
        ),
    )
    val pollToExport = poll.copy(ballots = emptyList())
    val pollBytes = Cbor.encodeToByteArray(pollToExport)
    val compressedPollBytes = compress(pollBytes)
    val compressedPollString = encode(compressedPollBytes)

    val pollQrImportViewModel = viewModel {
        PollQrImportViewModel(
            pollDataSource = InMemoryPollDataSource(), // dummy
        )
    }
    pollQrImportViewModel.initialize(
        context = LocalContext.current,
        qrUriPath = compressedPollString,
    )
    val state = pollQrImportViewModel.viewState.collectAsState().value

    JmTheme {
        PollQrImportScreen(
            modifier = modifier,
            state = state,
        )
    }
}
