package com.illiouchine.jm

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.illiouchine.jm.logic.HomeViewModel
import com.illiouchine.jm.logic.PollResultViewModel
import com.illiouchine.jm.logic.PollSetupViewModel
import com.illiouchine.jm.logic.PollVotingViewModel
import com.illiouchine.jm.logic.SettingsViewModel
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.screen.AboutScreen
import com.illiouchine.jm.ui.screen.HomeScreen
import com.illiouchine.jm.ui.screen.OnBoardingScreen
import com.illiouchine.jm.ui.screen.PollSetupScreen
import com.illiouchine.jm.ui.screen.PollVotingScreen
import com.illiouchine.jm.ui.screen.ResultScreen
import com.illiouchine.jm.ui.screen.SettingsScreen
import com.illiouchine.jm.ui.theme.JmTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModel()
    private val settingsViewModel: SettingsViewModel by viewModel()
    private val pollSetupViewModel: PollSetupViewModel by viewModel()
    private val pollVotingViewModel: PollVotingViewModel by viewModel()
    private val pollResultViewModel: PollResultViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val homeViewState by homeViewModel.homeViewState.collectAsState()
            val settingsState by settingsViewModel.settingsViewState.collectAsState()
            val pollSetupState by pollSetupViewModel.pollSetupViewState.collectAsState()
            val pollVotingViewState by pollVotingViewModel.pollVotingViewState.collectAsState()
            val pollResultViewState by pollResultViewModel.pollResultViewState.collectAsState()


            val navController = rememberNavController()

            // TODO: don't prevent lock at all times ; only during vote (and perhaps results)
            // Rule: the screen should never lock during the voting/result phase of the poll
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            JmTheme {
                NavHost(
                    navController = navController,
                    startDestination = Screens.Home.name,
                ) {
                    composable(Screens.Home.name) {
                        if (settingsState.showOnboarding) {
                            OnBoardingScreen(
                                modifier = Modifier,
                                onFinish = { settingsViewModel.updateShowOnBoarding(false) },
                            )
                        } else {
                            HomeScreen(
                                modifier = Modifier,
                                navController = navController,
                                homeViewState = homeViewState,
                                onSetupBlankPoll = {
                                    pollSetupViewModel.startPollSetup(PollConfig())
                                    navController.navigate(Screens.PollSetup.name)
                                },
                                onDeletePoll = {
                                    homeViewModel.deletePoll(it)
                                },
                                onSetupClonePoll = {
                                    pollSetupViewModel.startPollSetup(it.pollConfig)
                                    navController.navigate(Screens.PollSetup.name)
                                },
                                onVoteClonePoll = {
                                    pollVotingViewModel.initNewVotingSession(it.pollConfig)
                                    navController.navigate(Screens.PollVote.name)
                                },
                                onShowResult = {
                                    pollResultViewModel.finalizePoll(poll = it)
                                    navController.navigate(Screens.PollResult.name)
                                }
                            )
                        }
                    }
                    composable(Screens.PollSetup.name) {
                        PollSetupScreen(
                            modifier = Modifier,
                            navController = navController,
                            pollSetupState = pollSetupState,
                            onAddSubject = { pollSetupViewModel.onAddSubject(it) },
                            onAddProposal = { pollSetupViewModel.onAddProposal(it) },
                            onRemoveProposal = { pollSetupViewModel.onRemoveProposal(it) },
                            setupFinished = {
                                pollVotingViewModel.initNewVotingSession(pollSetupState.pollSetup)
                                navController.navigate(Screens.PollVote.name)
                            },
                            onDismissFeedback = { pollSetupViewModel.onDismissFeedback() },
                        )
                    }
                    composable(Screens.PollVote.name) {
                        PollVotingScreen(
                            modifier = Modifier,
                            pollVotingState = pollVotingViewState,
                            onStartVoting = { pollVotingViewModel.initParticipantVotingSession() },
                            onJudgmentCast = { pollVotingViewModel.onJudgmentCast(it) },
                            onBallotConfirmed = { pollVotingViewModel.onBallotConfirmed(it) },
                            onBallotCanceled = { pollVotingViewModel.onBallotCanceled() },
                            onCancelLastJudgment = { pollVotingViewModel.onCancelLastJudgment() },
                            onFinish = {
                                pollResultViewModel.finalizePoll(it)
                                homeViewModel.savePolls(it)
                                navController.navigate(Screens.PollResult.name)
                            },
                        )
                    }
                    composable(Screens.PollResult.name) {
                        if (pollResultViewState.poll != null) {
                            ResultScreen(
                                modifier = Modifier,
                                poll = pollResultViewState.poll!!,
                                onFinish = {
                                    navController.navigate(Screens.Home.name)
                                },
                            )
                        } else {
                            // During the screen navigation transition to home (in onFinish above),
                            // we end up here very briefly, 'cause concurrency probably.
                            // For now, nothing is cool I guess ?
                        }
                    }
                    composable(Screens.Settings.name) {
                        SettingsScreen(
                            modifier = Modifier,
                            navController = navController,
                            settingsState = settingsState,
                            onShowOnboardingChange = {
                                settingsViewModel.updateShowOnBoarding(it)
                            },
                            onDismissFeedback = {
                                //homeViewModel.onDismissFeedback()
                            },
                        )
                    }
                    composable(Screens.About.name) {
                        AboutScreen(
                            modifier = Modifier,
                            navController = navController,
                        )
                    }
                }
            }
        }
    }
}