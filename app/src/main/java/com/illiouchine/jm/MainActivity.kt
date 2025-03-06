package com.illiouchine.jm

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.illiouchine.jm.logic.HomeViewModel
import com.illiouchine.jm.logic.PollResultViewModel
import com.illiouchine.jm.logic.PollSetupViewModel
import com.illiouchine.jm.logic.PollVotingViewModel
import com.illiouchine.jm.logic.SettingsViewModel
import com.illiouchine.jm.ui.Navigator
import com.illiouchine.jm.ui.screen.AboutScreen
import com.illiouchine.jm.ui.screen.HomeScreen
import com.illiouchine.jm.ui.screen.OnBoardingScreen
import com.illiouchine.jm.ui.screen.PollSetupScreen
import com.illiouchine.jm.ui.screen.PollVotingScreen
import com.illiouchine.jm.ui.screen.ResultScreen
import com.illiouchine.jm.ui.screen.SettingsScreen
import com.illiouchine.jm.ui.theme.JmTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModel()
    private val settingsViewModel: SettingsViewModel by viewModel()
    private val pollSetupViewModel: PollSetupViewModel by viewModel()
    private val pollVotingViewModel: PollVotingViewModel by viewModel()
    private val pollResultViewModel: PollResultViewModel by viewModel()
    private val navigator: Navigator by inject()

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


            // Rule: the screen should never lock during the voting/result phase of the poll
            // Therefore, we clear the flag here and set it on in the appropriate screens.
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            JmTheme {

                // Handle navigation.
                LaunchedEffect("navigator") {
                    navigator.sharedFlow.onEach {
                        navController.navigate(it.name)
                    }.launchIn(this)
                }

                NavHost(
                    navController = navController,
                    startDestination = Navigator.Screens.Home.name,
                ) {
                    composable(Navigator.Screens.Home.name) {
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
                                onDeletePoll = {
                                    homeViewModel.deletePoll(it)
                                },
                                onSetupBlankPoll = {
                                    pollSetupViewModel.startPollSetup()
                                },
                                onSetupClonePoll = {
                                    pollSetupViewModel.startPollSetup(it.pollConfig)
                                },
                                onResumePoll = {
                                    pollVotingViewModel.resumeVotingSession(it)
                                },
                                onShowResult = {
                                    pollResultViewModel.finalizePoll(poll = it)
                                },
                            )
                        }
                    }
                    composable(Navigator.Screens.PollSetup.name) {
                        PollSetupScreen(
                            modifier = Modifier,
                            navController = navController,
                            pollSetupState = pollSetupState,
                            onAddSubject = { context, subject ->
                                pollSetupViewModel.onAddSubject(context, subject)
                            },
                            onAddProposal = { context, proposal ->
                                pollSetupViewModel.onAddProposal(context, proposal)
                            },
                            onRemoveProposal = { pollSetupViewModel.onRemoveProposal(it) },
                            onGradingSelected = { pollSetupViewModel.onGradingSelected(it) },
                            onSetupFinished = { pollConfig ->
                                pollVotingViewModel.initNewVotingSession(config = pollConfig)
                            },
                            onDismissFeedback = { pollSetupViewModel.onDismissFeedback() },
                            onGetSubjectSuggestion = { pollSetupViewModel.getSubjectSuggestion(it) },
                            onGetProposalSuggestion = { pollSetupViewModel.getProposalSuggestion(it) },
                            onClearSubjectSuggestion = { pollSetupViewModel.clearSubjectSuggestion() },
                            onClearProposalSuggestion = { pollSetupViewModel.clearProposalSuggestion() }
                        )
                    }
                    composable(Navigator.Screens.PollVote.name) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        val mediaPlayer = MediaPlayer.create(LocalContext.current, R.raw.success)
                        PollVotingScreen(
                            modifier = Modifier,
                            pollVotingState = pollVotingViewState,
                            onStartVoting = { pollVotingViewModel.initParticipantVotingSession() },
                            onJudgmentCast = { pollVotingViewModel.onJudgmentCast(it) },
                            onBallotConfirmed = {
                                pollVotingViewModel.onBallotConfirmed(it)
                                if (settingsState.playSound) {
                                    mediaPlayer.start()
                                }
                            },
                            onBallotCanceled = { pollVotingViewModel.onBallotCanceled() },
                            onCancelLastJudgment = { pollVotingViewModel.onCancelLastJudgment() },
                            onFinish = {
                                homeViewModel.savePolls(it)
                                pollResultViewModel.finalizePoll(it)
                            },
                        )
                    }
                    composable(Navigator.Screens.PollResult.name) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        if (pollResultViewState.poll != null) {
                            ResultScreen(
                                modifier = Modifier,
                                state = pollResultViewState,
                                onFinish = {
                                    navigator.navigateTo(Navigator.Screens.Home)
                                },
                            )
                        } else {
                            // During the screen navigation transition to home (in onFinish above),
                            // we end up here very briefly, 'cause concurrency probably.
                            // For now, nothing is cool I guess ?
                        }
                    }
                    composable(Navigator.Screens.Settings.name) {
                        SettingsScreen(
                            modifier = Modifier,
                            navController = navController,
                            settingsState = settingsState,
                            onShowOnboardingChange = {
                                settingsViewModel.updateShowOnBoarding(it)
                            },
                            onPlaySoundChange = {
                                settingsViewModel.updatePlaySound(it)
                            },
                            onDefaultGradingSelected = {
                                settingsViewModel.updateDefaultGrading(it)
                            },
                            onDismissFeedback = {
                                //homeViewModel.onDismissFeedback()
                            },
                        )
                    }
                    composable(Navigator.Screens.About.name) {
                        AboutScreen(
                            modifier = Modifier,
                            navController = navController,
                        )
                    }
                }
            }
        }
    }

    companion object {
        fun create(context: Context?): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}