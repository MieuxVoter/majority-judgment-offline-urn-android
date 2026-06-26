package com.illiouchine.jm.ui.screen

import android.content.ClipData
import android.content.res.Configuration
import android.graphics.Typeface.MONOSPACE
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.illiouchine.jm.R
import com.illiouchine.jm.data.InMemoryPollDataSource
import com.illiouchine.jm.logic.PollQrExportViewModel
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.composable.ScreenTitle
import com.illiouchine.jm.ui.composable.image.QrCodeImage
import com.illiouchine.jm.ui.composable.scaffold.MjuScaffold
import com.illiouchine.jm.ui.composable.spacer.MediumVerticalSpacer
import com.illiouchine.jm.ui.composable.spacer.SmallVerticalSpacer
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
@Composable
fun PollQrExportScreen(
    modifier: Modifier = Modifier,
    state: PollQrExportViewModel.PollQrExportViewState,
    onBack: () -> Unit = {},
) {
    MjuScaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->

        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()
        val clipboard: Clipboard = LocalClipboard.current

        Column(
            modifier = modifier
                .padding(paddingValues = innerPadding)
                .padding(horizontal = Theme.spacing.small)
                .fillMaxSize()
                .verticalScroll(state = scrollState),
        ) {
            ScreenTitle(
                text = stringResource(R.string.title_export_poll) + "\n" +
                    state.poll.pollConfig.subject + "\n" +
                    "(${state.poll.uuid?.toString()?.take(8)})",
            )

            if (state.poll.uuid == null) {
                Text("This poll is from another era and cannot be exported.  Please make a new one.")
                return@Column
            }

            Text(
                text = "With this experimental daisy-chaining feature, you may use multiple offline devices to collect ballots, which is useful in large assemblies."
            )
            if (!state.poll.ballots.isEmpty()) {
                Text(text = "The other devices will not see the ballots already recorded on this device.")
            }

            SmallVerticalSpacer()
            Text(
                text = "Step 1",
                fontSize = Theme.typography.headlineMedium.fontSize,
            )
            SmallVerticalSpacer()
            Text(text = "Scan this QR Code with another device that also has the MJ Urn installed:")

            if (state.pollQrBitmap != null) {
                MediumVerticalSpacer()

                QrCodeImage(
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally),
                    bitmap = state.pollQrBitmap,
                )

                SmallVerticalSpacer()
                Text(
                    "Here's a clickable excerpt of the content of this QR Code" + " " + "(${state.pollQrContent.length} characters)" + ":"
                )

                SmallVerticalSpacer()
                Text(
                    modifier = Modifier
                        .padding(horizontal = Theme.spacing.medium)
                        .clickable(
                            enabled = true,
                            onClick = {
                                coroutineScope.launch {
                                    clipboard.setClipEntry(
                                        ClipEntry(
                                            // This works as text, I don't know apps using HTML
                                            ClipData.newHtmlText(
                                                state.poll.pollConfig.subject,
                                                state.pollQrContent,
                                                "<a href=\"${state.pollQrContent}\">${state.poll.pollConfig.subject}</a>"
                                            )
                                        )
                                    )
                                }
                            },
                        ),
                    text = state.pollQrContent,
                    fontFamily = FontFamily(typeface = MONOSPACE),
                    maxLines = 1,
                    softWrap = false,
                )

                SmallVerticalSpacer()
                Text(
                    "The fact that it looks like an invocation of Creatures Beyond Time is perfectly normal, as it is compressed."
                )

                SmallVerticalSpacer()
                Text(
                    text = "Step 2",
                    fontSize = Theme.typography.headlineMedium.fontSize,
                )
                SmallVerticalSpacer()
                Text(
                    "Once the other devices are done collecting ballots, scan with this device the QR Codes they can each generate when using the Export Ballots feature."
                )

                SmallVerticalSpacer()
                Text(
                    "We do not provide a QR Code scanner with this app, since we do not want it to access the Camera.  Your favorite Qr Code scanner should work."
                )
            } else {
                MediumVerticalSpacer()
                Text("There was an error generating your Qr Code.")
            }

            MediumVerticalSpacer()

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onBack,
            ) { Text(stringResource(R.string.button_finish)) }

            MediumVerticalSpacer()
        }
    }
}

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
// @PreviewScreenSizes // my eyes hurt ← no dark mode
@Composable
fun PreviewPollQrExportScreen(modifier: Modifier = Modifier) {
    val poll = Poll(
        id = 17,
        pollConfig = PollConfig(
            subject = "\uD83D\uDD2E Présidence Française 2027",
            proposals = listOf(
                "Jean-Luc Mélenchon",
                "Gabriel Attal",
                "Fabien Roussel",
                "Édouard Philippe",
                "Chloé Ridel",
                "Jordan Bardella",
                "François Ruffin",
                "François Asselineau",
                "Philippe Poutou",
                "Bruno Retailleau",
                "Monsieur Patate",
            ),
            grading = Grading.Quality7Grading,
        ),
    )

    val pollQrExportViewModel = viewModel {
        PollQrExportViewModel(
            pollDataSource = InMemoryPollDataSource(), // dummy
        )
    }
    pollQrExportViewModel.initializeFromPoll(poll)
    val state = pollQrExportViewModel.viewState.collectAsState().value

    JmTheme {
        PollQrExportScreen(
            modifier = modifier,
            state = state,
        )
    }
}
