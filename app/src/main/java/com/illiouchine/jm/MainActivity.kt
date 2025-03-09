package com.illiouchine.jm

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.illiouchine.jm.logic.HomeViewModel
import com.illiouchine.jm.logic.PollResultViewModel
import com.illiouchine.jm.logic.PollSetupViewModel
import com.illiouchine.jm.logic.PollVotingViewModel
import com.illiouchine.jm.logic.SettingsViewModel
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.Destination
import com.illiouchine.jm.ui.NavigationAction
import com.illiouchine.jm.ui.Navigator
import com.illiouchine.jm.ui.ObserveAsEvents
import com.illiouchine.jm.ui.Screens
import com.illiouchine.jm.ui.screen.AboutScreen
import com.illiouchine.jm.ui.screen.HomeScreen
import com.illiouchine.jm.ui.screen.OnBoardingScreen
import com.illiouchine.jm.ui.screen.PollSetupScreen
import com.illiouchine.jm.ui.screen.PollVotingScreen
import com.illiouchine.jm.ui.screen.ResultScreen
import com.illiouchine.jm.ui.screen.SettingsScreen
import com.illiouchine.jm.ui.theme.JmTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()
            val navigator : Navigator by inject<Navigator>()

            ObserveAsEvents(navigator.navigationAction) { action ->
                when(action) {
                    is NavigationAction.Navigate -> navController.navigate(
                        action.destination
                    ) {
                        action.navOptions(this)
                    }
                    NavigationAction.NavigateUp -> navController.navigateUp()
                }
            }


            // Rule: the screen should never lock during the voting/result phase of the poll
            // Therefore, we clear the flag here and set it on in the appropriate screens.
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            JmTheme {
                NavHost(
                    navController = navController,
                    startDestination = navigator.startDestination,
                ) {
                    composable<Destination.Home> {
                        val homeViewModel: HomeViewModel by viewModel()
                        homeViewModel.loadPolls()
                        val homeViewState by homeViewModel.homeViewState.collectAsState()

                        if (homeViewState.showOnboarding) {
                            OnBoardingScreen(
                                modifier = Modifier,
                                onFinish = { homeViewModel.onOnboardingFinished() },
                            )
                        } else {
                            HomeScreen(
                                modifier = Modifier,
                                navController = navController,
                                homeViewState = homeViewState,
                                onDeletePoll = {
                                    homeViewModel.deletePoll(it)
                                },
                                onSetupBlankPoll = {
                                    homeViewModel.setupBlankPoll()
                                },
                                onSetupClonePoll = { poll: Poll ->
                                    homeViewModel.setupPoll(poll)
                                },
                                onResumePoll = { poll: Poll ->
                                    homeViewModel.resumePoll(poll)
                                },
                                onShowResult = { poll: Poll ->
                                    homeViewModel.showResult(poll)
                                },
                            )
                        }
                    }
                    composable<Destination.PollSetup>() {
                        val pollSetupViewModel: PollSetupViewModel by viewModel()
                        val pollSetupViewState by pollSetupViewModel.pollSetupViewState.collectAsState()

                        PollSetupScreen(
                            modifier = Modifier,
                            navController = navController,
                            pollSetupState = pollSetupViewState,
                            onAddSubject = { context, subject ->
                                pollSetupViewModel.addSubject(context, subject)
                            },
                            onAddProposal = { context, proposal ->
                                pollSetupViewModel.addProposal(context, proposal)
                            },
                            onRemoveProposal = { pollSetupViewModel.removeProposal(it) },
                            onGradingSelected = { pollSetupViewModel.selectGrading(it) },
                            onSetupFinished = { pollSetupViewModel.finishSetup(it) },
                            onDismissFeedback = { pollSetupViewModel.clearFeedback() },
                            onGetSubjectSuggestion = {
                                pollSetupViewModel.refreshSubjectSuggestion(it)
                            },
                            onGetProposalSuggestion = {
                                pollSetupViewModel.refreshProposalSuggestion(it)
                            },
                            onClearSubjectSuggestion = { pollSetupViewModel.clearSubjectSuggestion() },
                            onClearProposalSuggestion = { pollSetupViewModel.clearProposalSuggestion() },
                        )
                    }
                    composable<Destination.PollVote> { backStackEntry ->

                        val pollVotingViewModel: PollVotingViewModel by viewModel()
                        val pollVotingViewState by pollVotingViewModel.pollVotingViewState.collectAsState()

                        val pollVote: Screens.PollVote = backStackEntry.toRoute()
                        LaunchedEffect(pollVote) {
                            pollVotingViewModel.initVotingSessionForPoll(
                                pollVote.pollId,
                            )
                        }

                        LaunchedEffect("screen_pinning") {
                            perhapsLockScreen(pollVotingViewState)
                        }

                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                        PollVotingScreen(
                            modifier = Modifier,
                            pollVotingState = pollVotingViewState,
                            onStartVoting = { pollVotingViewModel.initParticipantVotingSession() },
                            onJudgmentCast = { pollVotingViewModel.castJudgment(it) },
                            onBallotConfirmed = { context, ballot ->
                                pollVotingViewModel.confirmBallot(
                                    context = context,
                                    ballot = ballot,
                                )
                            },
                            onBallotCanceled = { pollVotingViewModel.cancelBallot() },
                            onCancelLastJudgment = { pollVotingViewModel.cancelLastJudgment() },
                            onFinish = { pollVotingViewModel.finalizePoll() },
                        )
                    }
                    composable<Destination.PollResult> { backStackEntry ->

                        val pollResultViewModel: PollResultViewModel by viewModel()
                        val pollResultViewState by pollResultViewModel.pollResultViewState.collectAsState()

                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        if (pollResultViewState.poll != null) {
                            ResultScreen(
                                modifier = Modifier,
                                state = pollResultViewState,
                                onFinish = {
                                    pollResultViewModel.returnHome()
                                           },
                            )
                        } else {
                            // During the screen navigation transition to home (in onFinish above),
                            // we end up here very briefly, 'cause concurrency probably.
                            // For now, nothing is cool I guess ?
                        }
                    }
                    composable<Destination.Settings> {
                        val settingsViewModel: SettingsViewModel by viewModel()
                        val settingsViewState by settingsViewModel.settingsViewState.collectAsState()

                        SettingsScreen(
                            modifier = Modifier,
                            navController = navController,
                            settingsState = settingsViewState,
                            onShowOnboardingChange = {
                                settingsViewModel.updateShowOnboarding(it)
                            },
                            onPlaySoundChange = {
                                settingsViewModel.updatePlaySound(it)
                            },
                            onPinScreenChange = {
                                settingsViewModel.updatePinScreen(it)
                            },
                            onDefaultGradingSelected = {
                                settingsViewModel.updateDefaultGrading(it)
                            },
                            onDismissFeedback = {
                                //homeViewModel.onDismissFeedback()
                            },
                        )
                    }
                    composable<Destination.About> {
                        AboutScreen(
                            modifier = Modifier,
                            navController = navController,
                        )
                    }
                }
            }
        }
    }

    private fun perhapsLockScreen(
        pollVotingViewState: PollVotingViewModel.PollVotingViewState,
    ) {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (
            pollVotingViewState.pinScreen
            &&
            pollVotingViewState.isInStateReady() // can probably be removed safely now
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