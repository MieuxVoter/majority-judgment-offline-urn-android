package com.illiouchine.jm.ui.screen

import android.content.ClipData
import android.content.res.Configuration
import android.graphics.Typeface.MONOSPACE
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.min
import androidx.lifecycle.viewmodel.compose.viewModel
import com.illiouchine.jm.R
import com.illiouchine.jm.data.InMemoryPollDataSource
import com.illiouchine.jm.logic.BallotsQrExportViewModel
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Judgment
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.composable.ScreenTitle
import com.illiouchine.jm.ui.composable.scaffold.MjuScaffold
import com.illiouchine.jm.ui.composable.spacer.MediumVerticalSpacer
import com.illiouchine.jm.ui.composable.spacer.SmallVerticalSpacer
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

@Composable
fun BallotsQrExportScreen(
    modifier: Modifier = Modifier,
    state: BallotsQrExportViewModel.ViewState,
    onBack: () -> Unit = {},
) {
    MjuScaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->

        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()
        val clipboard = LocalClipboard.current
        val winSize = LocalWindowInfo.current.containerDpSize
        // val context = LocalContext.current

        @Composable
        fun ColumnScope.FinishButton() {
            Button(
                modifier = Modifier
                    .padding(vertical = Theme.spacing.medium)
                    .align(Alignment.CenterHorizontally),
                onClick = onBack,
            ) { Text(stringResource(R.string.button_finish)) }
        }

        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = Theme.spacing.small)
                .fillMaxSize()
                .verticalScroll(state = scrollState),
        ) {
            val subtitle = buildString {
                if (state.poll != null) {
                    append("\n${state.poll.pollConfig.subject}")
                }
                if (state.poll?.uuid != null) {
                    append("\n(${state.poll.uuid.toString().take(8)})")
                }
            }

            ScreenTitle(
                text = stringResource(R.string.action_export_ballots) + subtitle,
            )

            if (state.errorMessage != null) {
                Text(state.errorMessage)
                FinishButton()
                return@Column
            }

            if (state.poll == null) {
                Text("The poll does not exist.")
                FinishButton()
                return@Column
            }

            if (state.poll.uuid == null) {
                Text(
                    "This poll is from another era and its ballots cannot be exported.  " +
                        "Please make a new one."
                )
                FinishButton()
                return@Column
            }

            if (state.qrExports.isEmpty()) {
                Text("There was an error generating the QR Code.")
                Text("Please nag the devs to implement a proper error journal.")
                FinishButton()
                return@Column
            }

            Text(
                text = "Scan this QR Code with another device that also has that poll, " +
                    "to send it your ballots."
            )

            for (qrExport in state.qrExports) {
                if (qrExport.ballotsDto == null) {
                    continue // if this happens, something is very wrong with the code
                }
                if (qrExport.qrContent == null) {
                    continue // TBD: perhaps show a brief error message?
                }
                if (qrExport.qrBitmap == null) {
                    continue // TBD: perhaps show a brief error message?
                }

                MediumVerticalSpacer()

                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                        .size(min(winSize.width, winSize.height) * 0.9f)
                        .align(alignment = Alignment.CenterHorizontally),
                    bitmap = qrExport.qrBitmap,
                    contentDescription = "QR Code",
                )

                SmallVerticalSpacer()

                Text(
                    "Here's a clickable excerpt of the content of this QR Code" +
                        " " + "(${qrExport.qrContent.length} characters, " +
                        "${qrExport.qrContent.encodeToByteArray().size} octets)" +
                        ":"
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
                                            // This works as text; I don't know of apps using HTML.
                                            // Still, might be worth a try.
                                            ClipData.newHtmlText(
                                                "Ballots",
                                                qrExport.qrContent,
                                                "<a href=\"${qrExport.qrContent}\">" +
                                                    "Ballots of ${state.poll.pollConfig.subject}" +
                                                    "</a>"
                                            )
                                        )
                                    )
                                }
                            },
                        ),
                    text = qrExport.qrContent,
                    fontFamily = FontFamily(typeface = MONOSPACE),
                    maxLines = 1,
                    softWrap = false,
                )
            }

            FinishButton()
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
@Composable
@Suppress("SpellCheckingInspection")
fun PreviewBallotsQrExportScreen(modifier: Modifier = Modifier) {
    val poll = Poll(
        id = 42,
        uuid = UUID(1234567890123456789, 1234567890123456789),
        pollConfig = PollConfig(
            subject = "Musique de cette nuit",
            proposals = listOf(
                "La Phaze",
                "Keny Arkana",
                "Stupeflip",
                "Prodigy",
            ),
            grading = Grading.Quality7Grading,
        ),
        ballots = buildList {
            val rng = Random(42)
            for (i in 1..40) {
                add(
                    Ballot(
                        uuid = UUID(i.toLong(), 0),
                        judgments = listOf(
                            Judgment(proposal = 0, grade = rng.nextInt(0, 7)),
                            Judgment(proposal = 1, grade = rng.nextInt(0, 7)),
                            Judgment(proposal = 2, grade = rng.nextInt(0, 7)),
                            Judgment(proposal = 3, grade = rng.nextInt(0, 7)),
                        ),
                    )
                )
            }
        },
    )
    val pollDataSource = InMemoryPollDataSource()

    val ballotsQrExportViewModel = viewModel {
        BallotsQrExportViewModel(
            pollDataSource = pollDataSource,
        )
    }
    ballotsQrExportViewModel.initializeFromPoll(
        context = LocalContext.current,
        poll = poll,
    )
    val state = ballotsQrExportViewModel.viewState.collectAsState().value

    JmTheme {
        BallotsQrExportScreen(
            modifier = modifier,
            state = state,
        )
    }
}
