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
import com.illiouchine.jm.model.Ballot
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.CustomNavType
import com.illiouchine.jm.ui.Navigator
import com.illiouchine.jm.ui.Screens
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
import kotlin.reflect.typeOf

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModel()
    private val settingsViewModel: SettingsViewModel by viewModel()
    private val pollSetupViewModel: PollSetupViewModel by viewModel()
    private val pollVotingViewModel: PollVotingViewModel by viewModel()
    private val pollResultViewModel: PollResultViewModel by viewModel()
    private val navigator: Navigator by inject()

    fun perhapsLockScreen(
        pollVotingViewState: PollVotingViewModel.PollVotingViewState,
        settingsViewState: SettingsViewModel.SettingsViewState,
    ) {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (
            settingsViewState.pinScreen
            &&
            pollVotingViewState.isInStateReady() // can probably be removed safely now
            &&
            activityManager.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_NONE
        ) {
            startLockTask()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val homeViewState by homeViewModel.homeViewState.collectAsState()
            val settingsViewState by settingsViewModel.settingsViewState.collectAsState()
            val pollSetupViewState by pollSetupViewModel.pollSetupViewState.collectAsState()
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
                        //Log.d("WGU", "Navigator triggered with destination : $it")
                        navController.navigate(it)
                    }.launchIn(this)
                }

                NavHost(
                    navController = navController,
                    startDestination = Screens.Home,
                ) {
                    composable<Screens.Home> {

                        homeViewModel.loadPolls()

                        if (settingsViewState.showOnboarding) {
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
                                    navigator.navigateTo(Screens.PollSetup())
                                },
                                onSetupClonePoll = { poll: Poll ->
                                    navigator.navigateTo(Screens.PollSetup(config = poll.pollConfig))
                                },
                                onResumePoll = { poll: Poll ->
                                    navigator.navigateTo(
                                        Screens.PollVote(
                                            config = poll.pollConfig,
                                            ballots = poll.ballots,
                                        )
                                    )
                                },
                                onShowResult = { poll: Poll ->
                                    navigator.navigateTo(Screens.PollResult(poll = poll))
                                },
                            )
                        }
                    }
                    composable<Screens.PollSetup>(
                        typeMap = mapOf(
                            typeOf<PollConfig?>() to CustomNavType.NullablePollConfigType,
                        )
                    ) { backStackEntry ->
                        val pollSetup = backStackEntry.toRoute<Screens.PollSetup>()
                        LaunchedEffect(pollSetup) {
                            pollSetupViewModel.initWithConfig(config = pollSetup.config)
                        }

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
                    composable<Screens.PollVote>(
                        typeMap = mapOf(
                            typeOf<PollConfig>() to CustomNavType.PollConfigType,
                            typeOf<List<Ballot>>() to CustomNavType.Ballots,
                        )
                    ) { backStackEntry ->

                        val pollVote: Screens.PollVote = backStackEntry.toRoute()
                        LaunchedEffect(pollVote) {
                            pollVotingViewModel.initVotingSession(
                                config = pollVote.config,
                                ballots = pollVote.ballots,
                            )
                            perhapsLockScreen(
                                pollVotingViewState,
                                settingsViewState,
                            )
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
                    composable<Screens.PollResult>(
                        typeMap = mapOf(
                            typeOf<Poll>() to CustomNavType.Poll,
                        )
                    ) { backStackEntry ->

                        val pollResult: Screens.PollResult = backStackEntry.toRoute()
                        LaunchedEffect(pollResult) {
                            pollResultViewModel.initializePollResult(poll = pollResult.poll)
                        }

                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        if (pollResultViewState.poll != null) {
                            ResultScreen(
                                modifier = Modifier,
                                state = pollResultViewState,
                                onFinish = { navigator.navigateTo(Screens.Home) },
                            )
                        } else {
                            // During the screen navigation transition to home (in onFinish above),
                            // we end up here very briefly, 'cause concurrency probably.
                            // For now, nothing is cool I guess ?
                        }
                    }
                    composable<Screens.Settings> {
                        SettingsScreen(
                            modifier = Modifier,
                            navController = navController,
                            settingsState = settingsViewState,
                            onShowOnboardingChange = {
                                settingsViewModel.updateShowOnBoarding(it)
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
                    composable<Screens.About> {
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