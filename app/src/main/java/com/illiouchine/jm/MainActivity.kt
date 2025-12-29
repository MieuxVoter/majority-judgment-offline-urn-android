package com.illiouchine.jm

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.illiouchine.jm.logic.HomeViewModel
import com.illiouchine.jm.logic.OnBoardingViewModel
import com.illiouchine.jm.logic.PollResultViewModel
import com.illiouchine.jm.logic.PollSetupViewModel
import com.illiouchine.jm.logic.PollVotingViewModel
import com.illiouchine.jm.logic.SettingsViewModel
import com.illiouchine.jm.ui.Screens
import com.illiouchine.jm.ui.TopLevelBackStack
import com.illiouchine.jm.ui.screen.AboutScreen
import com.illiouchine.jm.ui.screen.HomeScreen
import com.illiouchine.jm.ui.screen.LoaderScreen
import com.illiouchine.jm.ui.screen.OnBoardingScreen
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

            val topLevelBackStack = remember { TopLevelBackStack(Screens.Home) }

            JmTheme {
                NavDisplay(
                    backStack = topLevelBackStack.backStack,
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
                                    topLevelBackStack.catch(event)
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
                            )
                        }
                        entry<Screens.About> {
                            AboutScreen(
                                modifier = Modifier,
                                onShowSpirograph = { topLevelBackStack.add(Screens.Loader) },
                                onBottomBarItemSelected = { topLevelBackStack.switchTopLevel(it) },
                            )
                        }
                        entry<Screens.Loader>{
                            LoaderScreen(
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                        entry<Screens.OnBoarding> {
                            val onBoardingViewModel: OnBoardingViewModel by viewModel()
                            LaunchedEffect(Unit) {
                                onBoardingViewModel.navEvents.collect { event ->
                                    topLevelBackStack.catch(event)
                                }
                            }
                            OnBoardingScreen(
                                onFinish = onBoardingViewModel::finish
                            )
                        }
                        entry<Screens.PollResult> { key ->
                            val pollResultViewModel: PollResultViewModel by viewModel()
                            val pollResultViewState by pollResultViewModel.pollResultViewState.collectAsState()

                            val context = LocalContext.current
                            LaunchedEffect(key.id) {
                                pollResultViewModel.initializePollResultById(
                                    context = context,
                                    pollId = key.id,
                                )
                            }

                            LaunchedEffect(Unit) {
                                pollResultViewModel.navEvents.collect { event ->
                                    topLevelBackStack.catch(event)
                                }
                            }

                            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                            if (pollResultViewState.poll != null) {
                                ResultScreen(
                                    modifier = Modifier,
                                    state = pollResultViewState,
                                    onFinish = pollResultViewModel::onFinish,
                                    onShowProportionsHelp = { topLevelBackStack.add(Screens.ProportionsHelp) },
                                )
                            }
                        }
                        entry<Screens.PollSetup> { key ->
                            val pollSetupViewModel: PollSetupViewModel by viewModel()
                            val pollSetupViewState by pollSetupViewModel.pollSetupViewState.collectAsState()

                            LaunchedEffect(Unit) {
                                pollSetupViewModel.navEvents.collect { event ->
                                    topLevelBackStack.catch(event)
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
                                onBottomBarItemSelected = {topLevelBackStack.switchTopLevel(it)},
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
                                    topLevelBackStack.catch(event)
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
                                    topLevelBackStack.catch(event)
                                }
                            }

                            SideEffect { settingsViewModel.initialize() }

                            SettingsScreen(
                                modifier = Modifier,
                                onBottomBarItemSelected = {topLevelBackStack.switchTopLevel(it)},
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
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (
            pinScreen
            &&
            activityManager.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_NONE
        ) {
            startLockTask()
        }
    }

    companion object {
        fun create(context: Context?): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
