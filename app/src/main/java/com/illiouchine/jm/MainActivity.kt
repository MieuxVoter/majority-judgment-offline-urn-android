package com.illiouchine.jm

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.illiouchine.jm.filters.BallotsFilterInterface
import com.illiouchine.jm.filters.NoBallotsFilter
import com.illiouchine.jm.logic.BallotsQrExportViewModel
import com.illiouchine.jm.logic.BallotsQrImportViewModel
import com.illiouchine.jm.logic.HomeViewModel
import com.illiouchine.jm.logic.OnboardingViewModel
import com.illiouchine.jm.logic.PollQrExportViewModel
import com.illiouchine.jm.logic.PollQrImportViewModel
import com.illiouchine.jm.logic.PollResultViewModel
import com.illiouchine.jm.logic.PollSetupViewModel
import com.illiouchine.jm.logic.PollVotingViewModel
import com.illiouchine.jm.logic.SettingsViewModel
import com.illiouchine.jm.ui.navigator.Screens
import com.illiouchine.jm.ui.navigator.TopLevelBackStack
import com.illiouchine.jm.ui.screen.AboutScreen
import com.illiouchine.jm.ui.screen.BallotsQrExportScreen
import com.illiouchine.jm.ui.screen.BallotsQrImportScreen
import com.illiouchine.jm.ui.screen.HomeScreen
import com.illiouchine.jm.ui.screen.LoaderScreen
import com.illiouchine.jm.ui.screen.OnBoardingScreen
import com.illiouchine.jm.ui.screen.PollQrExportScreen
import com.illiouchine.jm.ui.screen.PollQrImportScreen
import com.illiouchine.jm.ui.screen.PollSetupScreen
import com.illiouchine.jm.ui.screen.PollVotingScreen
import com.illiouchine.jm.ui.screen.ProportionsHelpScreen
import com.illiouchine.jm.ui.screen.ResultScreen
import com.illiouchine.jm.ui.screen.SettingsScreen
import com.illiouchine.jm.ui.theme.JmTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            // Rule: the screen should never lock during the voting/result phase of the poll
            // Therefore, we clear the flag here and set it on in the appropriate screens.
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            // Retained variables survive activity recreations (eg: orientation change)
            // https://developer.android.com/develop/ui/compose/state-lifespans
            val topLevelBackStack = retain { TopLevelBackStack(Screens.Home) }

            val context = applicationContext
            val intent = getIntent()
            if (Intent.ACTION_VIEW == intent.action) {
                // We arrived here via QR Code scanning.  (We do not get to choose the action.)
                // We do not want to run another activity instance, so we GTFO.
                // This intent will either recall the existing activity or run a new one.
                val mainIntent = Intent(this, MainActivity::class.java)
                mainIntent.setAction(Intent.ACTION_MAIN)
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                mainIntent.setData(intent.data)
                startActivity(mainIntent)
                finish()
            } else if (Intent.ACTION_MAIN == intent.action) {
                val uri = intent.data
                if (uri != null) {
                    LaunchedEffect(uri.toString()) {
                        // Debug
                        // Toast.makeText(context, uri.host, Toast.LENGTH_LONG).show()
                        // Toast.makeText(context, uri.path, Toast.LENGTH_LONG).show()

                        if (uri.host == "p") {
                            // p is for Poll ; let's import the poll if we can
                            val data = uri.path
                            if (data != null) {
                                val compressedDataString = data.substring(1)
                                topLevelBackStack.add(
                                    Screens.PollQrImport(
                                        encodedContent = compressedDataString,
                                    )
                                )
                            }
                        } else if (uri.host == "b") {
                            // b is for Ballots ; let's import the ballots if we can
                            val data = uri.path
                            if (data != null) {
                                val compressedDataString = data.substring(1)
                                topLevelBackStack.add(
                                    Screens.BallotsQrImport(
                                        encodedContent = compressedDataString,
                                    )
                                )
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Error: unsupported URI host.",
                                Toast.LENGTH_LONG,
                            ).show()
                        }
                    }
                }
            }

            JmTheme {
                NavDisplay(
                    backStack = topLevelBackStack.currentBackStack,
                    onBack = { topLevelBackStack.removeLast() },
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                    ),
                    entryProvider = entryProvider {
                        entry<Screens.Home> {
                            val homeViewModel: HomeViewModel by viewModel()
                            val homeViewState by homeViewModel.homeViewState.collectAsState()

                            SideEffect { homeViewModel.initialize() }

                            LaunchedEffect(Unit) {
                                homeViewModel.navEvents.collect { event ->
                                    topLevelBackStack.handle(event)
                                }
                            }

                            HomeScreen(
                                modifier = Modifier,
                                onBottomBarItemSelected = { topLevelBackStack.switchTopLevel(it) },
                                homeViewState = homeViewState,
                                onSetupBlankPoll = homeViewModel::setupBlankPoll,
                                onSetupTemplatePoll = homeViewModel::setupPollFromTemplate,
                                onSetupClonePoll = homeViewModel::clonePoll,
                                onResumePoll = homeViewModel::resumePoll,
                                onShowResult = homeViewModel::showResult,
                                onDeletePoll = homeViewModel::deletePoll,
                                onExportPoll = homeViewModel::exportPoll,
                                onExportBallots = homeViewModel::exportBallots,
                            )
                        }
                        entry<Screens.About> {
                            AboutScreen(
                                modifier = Modifier,
                                onShowSpirograph = { topLevelBackStack.add(Screens.Loader) },
                                onBottomBarItemSelected = { topLevelBackStack.switchTopLevel(it) },
                            )
                        }
                        entry<Screens.Loader> {
                            LoaderScreen(
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                        entry<Screens.Onboarding> {
                            val onBoardingViewModel: OnboardingViewModel by viewModel()
                            LaunchedEffect(Unit) {
                                onBoardingViewModel.navEvents.collect { event ->
                                    topLevelBackStack.handle(event)
                                }
                            }
                            OnBoardingScreen(
                                onFinish = onBoardingViewModel::finish,
                            )
                        }
                        entry<Screens.PollSetup> { key ->
                            val pollSetupViewModel: PollSetupViewModel by viewModel()
                            val pollSetupViewState by pollSetupViewModel.pollSetupViewState.collectAsState()

                            LaunchedEffect(Unit) {
                                pollSetupViewModel.navEvents.collect { event ->
                                    topLevelBackStack.handle(event)
                                }
                            }

                            LaunchedEffect(key.hashCode()) {
                                pollSetupViewModel.initialize(
                                    cloneablePollId = key.cloneablePollId,
                                    pollTemplateSlug = key.pollTemplateSlug,
                                )
                            }

                            PollSetupScreen(
                                modifier = Modifier,
                                onBottomBarItemSelected = { topLevelBackStack.switchTopLevel(it) },
                                pollSetupState = pollSetupViewState,
                                onAddSubject = pollSetupViewModel::addSubject,
                                onAddProposal = pollSetupViewModel::addProposal,
                                onRemoveProposal = pollSetupViewModel::removeProposal,
                                onGradingSelected = pollSetupViewModel::selectGrading,
                                onSetupFinished = pollSetupViewModel::finishSetup,
                                onDismissFeedback = pollSetupViewModel::clearFeedback,
                                onGetSubjectSuggestion = pollSetupViewModel::refreshSubjectSuggestion,
                                onGetProposalSuggestion = pollSetupViewModel::refreshProposalSuggestion,
                                onClearSubjectSuggestion = pollSetupViewModel::clearSubjectSuggestion,
                                onClearProposalSuggestion = pollSetupViewModel::clearProposalSuggestion,
                            )
                        }
                        entry<Screens.PollVote> { key ->
                            val pollVotingViewModel: PollVotingViewModel by viewModel()
                            val pollVotingViewState by pollVotingViewModel.pollVotingViewState.collectAsState()

                            LaunchedEffect(Unit) {
                                pollVotingViewModel.navEvents.collect { event ->
                                    topLevelBackStack.handle(event)
                                }
                            }

                            LaunchedEffect(key.id) {
                                pollVotingViewModel.initVotingSessionForPoll(key.id)
                            }

                            // Hotfix: Wait for the state to be updated by initVotingSessionForPoll
                            // Otherwise pollVotingViewState.pinScreens may be true when it should not,
                            // albeit only once after setting the "pin auto." Setting to false.
                            val waitAnimation = remember { Animatable(0f) }
                            LaunchedEffect(pollVotingViewState.pinScreens) {
                                waitAnimation.animateTo(1f, tween(1000))
                                perhapsLockScreen(pollVotingViewState.pinScreens)
                            }

                            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                            PollVotingScreen(
                                modifier = Modifier,
                                pollVotingState = pollVotingViewState,
                                onStartVoting = pollVotingViewModel::initParticipantVotingSession,
                                onJudgmentCast = pollVotingViewModel::castJudgment,
                                onBallotConfirmed = pollVotingViewModel::confirmBallot,
                                onBallotCanceled = pollVotingViewModel::cancelBallot,
                                onCancelLastJudgment = pollVotingViewModel::cancelLastJudgment,
                                onFinish = pollVotingViewModel::finalizePoll,
                                onTryToGoBack = pollVotingViewModel::tryToGoBack,
                            )
                        }
                        entry<Screens.PollResult> { key ->
                            val context = LocalContext.current
                            val pollResultViewModel: PollResultViewModel by viewModel()

                            // TBD: how to make this rememberSaveable ?
                            // (anyhow soon we'll have a whole list/tree of filters)
                            var ballotsFilter by remember(key.id) {
                                mutableStateOf<BallotsFilterInterface>(NoBallotsFilter())
                            }

                            LaunchedEffect(key.id, ballotsFilter) {
                                pollResultViewModel.initializePollResultById(
                                    context = context,
                                    pollId = key.id,
                                    ballotFilter = ballotsFilter,
                                )
                            }

                            val pollResultViewState by pollResultViewModel.pollResultViewState.collectAsState()

                            LaunchedEffect(Unit) {
                                pollResultViewModel.navEvents.collect { event ->
                                    topLevelBackStack.handle(event)
                                }
                            }

                            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                            if (pollResultViewState.poll != null) {
                                ResultScreen(
                                    modifier = Modifier,
                                    state = pollResultViewState,
                                    onFinish = pollResultViewModel::onFinish,
                                    onShowProportionsHelp = {
                                        topLevelBackStack.add(Screens.ProportionsHelp)
                                    },
                                    onBallotsFilterUpdate = { filter ->
                                        ballotsFilter = filter

                                        // TBD: we should not do this here, yet we have to (?)
                                        pollResultViewModel.initializePollResultById(
                                            context = context,
                                            pollId = key.id,
                                            ballotFilter = ballotsFilter,
                                        )
                                    },
                                )
                            }
                        }
                        entry<Screens.PollQrExport> { key ->
                            val context = LocalContext.current
                            val viewModel: PollQrExportViewModel by viewModel()

                            LaunchedEffect(key.id) {
                                viewModel.initializeFromPollId(
                                    context = context,
                                    pollId = key.id,
                                )
                            }

                            val viewState by viewModel.viewState.collectAsState()

                            LaunchedEffect(Unit) {
                                viewModel.navEvents.collect { event ->
                                    topLevelBackStack.handle(event)
                                }
                            }

                            // Showing a QR Code in the Export screen; best keep the screen alight.
                            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                            if (viewState.hasPoll()) {
                                PollQrExportScreen(
                                    modifier = Modifier,
                                    state = viewState,
                                    onBack = viewModel::onBack,
                                )
                            } else {
                                // TBD: perhaps we should create a ErrorScreen ?
                                Text(text = "No poll to export.")
                            }
                        }
                        entry<Screens.PollQrImport> { key ->
                            val context = LocalContext.current
                            val viewModel: PollQrImportViewModel by viewModel()

                            LaunchedEffect(key.encodedContent) {
                                viewModel.initialize(
                                    context = context,
                                    qrUriPath = key.encodedContent,
                                )
                            }

                            val viewState by viewModel.viewState.collectAsState()

                            LaunchedEffect(Unit) {
                                viewModel.navEvents.collect { event ->
                                    topLevelBackStack.handle(event)
                                }
                            }

                            PollQrImportScreen(
                                modifier = Modifier,
                                state = viewState,
                                onConfirm = viewModel::onConfirm,
                                onCancel = viewModel::onCancel,
                            )
                        }
                        entry<Screens.BallotsQrExport> { key ->
                            val context = LocalContext.current
                            val viewModel: BallotsQrExportViewModel by viewModel()

                            LaunchedEffect(key.pollId) {
                                viewModel.initializeFromPollId(
                                    context = context,
                                    pollId = key.pollId,
                                )
                            }

                            val viewState by viewModel.viewState.collectAsState()

                            LaunchedEffect(Unit) {
                                viewModel.navEvents.collect { event ->
                                    topLevelBackStack.handle(event)
                                }
                            }

                            // Showing a QR Code in the Export screen; light the beacons!
                            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                            if (viewState.poll != null) {
                                BallotsQrExportScreen(
                                    modifier = Modifier,
                                    state = viewState,
                                    onBack = viewModel::onBack,
                                )
                            } else {
                                // TBD: perhaps we should create a ErrorScreen ?
                                Text(text = "No poll to export ballots of.")
                            }
                        }
                        entry<Screens.BallotsQrImport> { key ->
                            val context = LocalContext.current
                            val viewModel: BallotsQrImportViewModel by viewModel()

                            LaunchedEffect(key.encodedContent) {
                                viewModel.initialize(
                                    context = context,
                                    qrUriPath = key.encodedContent,
                                )
                            }

                            val viewState by viewModel.viewState.collectAsState()

                            LaunchedEffect(Unit) {
                                viewModel.navEvents.collect { event ->
                                    topLevelBackStack.handle(event)
                                }
                            }

                            BallotsQrImportScreen(
                                modifier = Modifier,
                                state = viewState,
                                onConfirm = viewModel::onConfirm,
                                onCancel = viewModel::onCancel,
                            )
                        }
                        entry<Screens.ProportionsHelp> {
                            ProportionsHelpScreen(
                                modifier = Modifier.fillMaxSize(),
                                onNavigateUp = { topLevelBackStack.removeLast() },
                            )
                        }
                        entry<Screens.Settings> {
                            val settingsViewModel: SettingsViewModel by viewModel()
                            val settingsViewState by settingsViewModel.settingsViewState.collectAsState()

                            LaunchedEffect(Unit) {
                                settingsViewModel.navEvents.collect { event ->
                                    topLevelBackStack.handle(event)
                                }
                            }

                            SideEffect { settingsViewModel.initialize() }

                            SettingsScreen(
                                modifier = Modifier,
                                onBottomBarItemSelected = { topLevelBackStack.switchTopLevel(it) },
                                settingsState = settingsViewState,
                                onShowOnBoardingRequested = settingsViewModel::showOnBoarding,
                                onPlaySoundChange = settingsViewModel::updatePlaySound,
                                onPinScreenChange = settingsViewModel::updatePinScreen,
                                onDefaultGradingSelected = settingsViewModel::updateDefaultGrading,
                                onDismissFeedback = {},
                            )
                        }
                    }
                )
            }
        }
    }

    private fun perhapsLockScreen(pinScreen: Boolean) {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        if (pinScreen && activityManager.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_NONE) {
            startLockTask()
        }
    }

    companion object {
        fun create(context: Context?): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
