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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.illiouchine.jm.R
import com.illiouchine.jm.data.InMemoryPollDataSource
import com.illiouchine.jm.logic.BallotsDto
import com.illiouchine.jm.logic.BallotsQrImportViewModel
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Grading
import com.illiouchine.jm.model.Judgment
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
import java.util.UUID

@Composable
fun AmountReportText(
    amount: String,
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        Text(
            text = amount,
            fontSize = Theme.typography.headlineLarge.fontSize,
            modifier = Modifier
                .alignBy(alignmentLine = FirstBaseline)
                .padding(end = Theme.spacing.small),
        )
        Text(
            text = text,
            modifier = Modifier.alignBy(alignmentLine = FirstBaseline),
        )
    }
}

@Composable
fun BallotsQrImportScreen(
    modifier: Modifier = Modifier,
    state: BallotsQrImportViewModel.ViewState,
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {},
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

        Column(
            modifier = modifier
                .padding(paddingValues = innerPadding)
                .padding(horizontal = Theme.spacing.small)
                .fillMaxSize()
                .verticalScroll(state = scrollState),
        ) {
            val subtitle = buildString {
                if (state.poll != null) {
                    append("\n${state.poll.pollConfig.subject}")
                }
                if (state.ballotsDto != null) {
                    append("\n(${state.ballotsDto.pollUuid.toString().take(8)})")
                }
            }

            ScreenTitle(
                text = stringResource(R.string.action_import_ballots) + subtitle,
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

            if (state.poll == null) {
                Text(
                    text = "The poll you're trying to import ballots for does not exist on this device.",
                )

                MediumVerticalSpacer()

                CancelAsPrimaryButton()

                return@Column
            }

            if (state.ballotsDto != null) {
                Text(
                    text = "You are about to import some ballots on this device. Here's a summary:",
                )

                MediumVerticalSpacer()

                val amountOfBallotsInPollBefore = state.poll.ballots.size
                val amountOfBallotsInImport = state.ballotsDto.ballots.size
                val existingBallotsUuids = state.poll.ballots.map { it.uuid }
                val ballotsSkipped = state.ballotsDto.ballots.filter {
                    existingBallotsUuids.contains(it.uuid)
                }
                val ballotsInvalidated = state.ballotsDto.ballots.filterNot {
                    state.poll.isBallotValid(it)
                }
                val amountOfBallotsSkipped = ballotsSkipped.size
                val amountOfBallotsInvalidated = ballotsInvalidated.size
                val amountOfBallotsImported =
                    amountOfBallotsInImport - amountOfBallotsSkipped - amountOfBallotsInvalidated
                val amountOfBallotsInPollAfter =
                    amountOfBallotsInPollBefore + amountOfBallotsImported

                AmountReportText(
                    amount = amountOfBallotsInImport.toString(),
                    text = "ballots in the import data",
                )
                MediumVerticalSpacer()

                if (amountOfBallotsInvalidated > 0) {
                    AmountReportText(
                        amount = amountOfBallotsInvalidated.toString(),
                        text = "invalid ballots will be skipped",
                    )
                    SmallVerticalSpacer()
                }
                AmountReportText(
                    amount = amountOfBallotsSkipped.toString(),
                    text = "ballots already in the poll will be skipped",
                )
                SmallVerticalSpacer()
                AmountReportText(
                    amount = amountOfBallotsImported.toString(),
                    text = "new ballots will be imported",
                )

                MediumVerticalSpacer()

                AmountReportText(
                    amount = amountOfBallotsInPollBefore.toString(),
                    text = "ballots in the poll before import",
                )
                SmallVerticalSpacer()
                AmountReportText(
                    amount = amountOfBallotsInPollAfter.toString(),
                    text = "ballots in the poll after import",
                )

                MediumVerticalSpacer()

                ActionRowCancelConfirm(
                    onCancel = onCancel,
                    onConfirm = onConfirm,
                )
            }
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
// @Preview(
//    name = "Phone (Portrait, Big Font)",
//    showSystemUi = true,
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    fontScale = 2.0f,
// )
// @Preview(
//    name = "Tablet",
//    device = "spec:width=1280dp,height=800dp,dpi=240",
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    showSystemUi = true,
// )
// @PreviewScreenSizes // my eyes hurt ← no dark mode
@Composable
fun PreviewBallotsQrImportScreen(modifier: Modifier = Modifier) {
    val poll = Poll(
        id = 17,
        uuid = UUID(66666669999999, 777111),
        pollConfig = PollConfig(
            subject = "Présidence Française 2027",
            proposals = listOf(
                "Jean-Luc Mélenchon",
                "Jordan Bardella",
                "Chloé Ridel",
            ),
            grading = Grading.Quality7Grading,
        ),
        ballots = listOf(
            Ballot(
                uuid = UUID(1, 0),
                judgments = listOf(
                    Judgment(proposal = 0, grade = 6),
                    Judgment(proposal = 1, grade = 0),
                    Judgment(proposal = 2, grade = 5),
                ),
            ),
            Ballot(
                uuid = UUID(2, 0),
                judgments = listOf(
                    Judgment(proposal = 0, grade = 5),
                    Judgment(proposal = 1, grade = 2),
                    Judgment(proposal = 2, grade = 4),
                ),
            ),
        ),
    )
    val ballotsDto = BallotsDto(
        pollUuid = UUID(66666669999999, 777111),
        ballots = listOf(
            Ballot(
                uuid = UUID(2, 0),
                judgments = listOf(
                    Judgment(proposal = 0, grade = 5),
                    Judgment(proposal = 1, grade = 2),
                    Judgment(proposal = 2, grade = 4),
                ),
            ),
            Ballot(
                uuid = UUID(3, 0),
                judgments = listOf(
                    Judgment(proposal = 0, grade = 3),
                    Judgment(proposal = 1, grade = 3),
                    Judgment(proposal = 2, grade = 3),
                ),
            ),
        ),
    )

    val pollDataSource = InMemoryPollDataSource()

    // TBD: could not use "by" here, for some reason
    val done = remember { mutableStateOf(value = false) }

    LaunchedEffect(Unit) {
        pollDataSource.savePoll(poll)
        done.value = true
    }

    val cborBytes = Cbor.encodeToByteArray(value = ballotsDto)
    val compressedBytes = compress(input = cborBytes)
    val uriPath = encode(compressedBytes)

    val ballotsQrImportViewModel = viewModel {
        BallotsQrImportViewModel(
            pollDataSource = pollDataSource,
        )
    }

    if (done.value) {
        ballotsQrImportViewModel.initialize(
            context = LocalContext.current,
            qrUriPath = uriPath,
        )
        val state = ballotsQrImportViewModel.viewState.collectAsState().value

        JmTheme {
            BallotsQrImportScreen(
                modifier = modifier,
                state = state,
            )
        }
    }
}
