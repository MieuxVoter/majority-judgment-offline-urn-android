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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.illiouchine.jm.logic.HomeViewModel
import com.illiouchine.jm.logic.PollResultViewModel
import com.illiouchine.jm.logic.PollSetupViewModel
import com.illiouchine.jm.logic.PollVotingViewModel
import com.illiouchine.jm.logic.SettingsViewModel
import com.illiouchine.jm.ui.NavigationAction
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
import com.illiouchine.jm.ui.utils.ObserveAsEvents
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            // Rule: the screen should never lock during the voting/result phase of the poll
            // Therefore, we clear the flag here and set it on in the appropriate screens.
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            val navController = rememberNavController()
            val navigator: Navigator by inject<Navigator>()

            // Handle navigation.
            ObserveAsEvents(navigator.navigationAction) { action ->
                when (action) {
                    is NavigationAction.Navigate -> navController.navigate(
                        action.destination
                    ) {
                        action.navOptions(this)
                    }

                    NavigationAction.NavigateUp -> navController.navigateUp()
                }
            }

            JmTheme {
                NavHost(
                    navController = navController,
                    startDestination = navigator.startDestination,
                ) {
                    composable<Screens.Home> {

                        val homeViewModel: HomeViewModel by viewModel()
                        val homeViewState by homeViewModel.homeViewState.collectAsState()

                        SideEffect { homeViewModel.initialize() }

                        if (homeViewState.showOnboarding) {
                            OnBoardingScreen(
                                modifier = Modifier,
                                onFinish = homeViewModel::finishOnboarding,
                            )
                        } else {
                            HomeScreen(
                                modifier = Modifier,
                                navController = navController,
                                homeViewState = homeViewState,
                                onDeletePoll = homeViewModel::deletePoll,
                                onSetupBlankPoll = homeViewModel::setupBlankPoll,
                                onSetupClonePoll = homeViewModel::clonePoll,
                                onResumePoll = homeViewModel::resumePoll,
                                onShowResult = homeViewModel::showResult,
                            )
                        }
                    }
                    composable<Screens.PollSetup> { backStackEntry ->

                        val pollSetupViewModel: PollSetupViewModel by viewModel()
                        val pollSetupViewState by pollSetupViewModel.pollSetupViewState.collectAsState()

                        val pollSetup = backStackEntry.toRoute<Screens.PollSetup>()
                        LaunchedEffect(pollSetup) {
                            pollSetupViewModel.initialize(pollId = pollSetup.id)
                        }

                        PollSetupScreen(
                            modifier = Modifier,
                            navController = navController,
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
                    composable<Screens.PollVote> { backStackEntry ->

                        val pollVotingViewModel: PollVotingViewModel by viewModel()
                        val pollVotingViewState by pollVotingViewModel.pollVotingViewState.collectAsState()

                        val pollVote: Screens.PollVote = backStackEntry.toRoute()
                        LaunchedEffect(pollVote) {
                            pollVotingViewModel.initVotingSessionForPoll(pollVote.id)
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
                        )
                    }
                    composable<Screens.PollResult> { backStackEntry ->

                        val pollResultViewModel: PollResultViewModel by viewModel()
                        val pollResultViewState by pollResultViewModel.pollResultViewState.collectAsState()

                        val context = LocalContext.current
                        val pollResult: Screens.PollResult = backStackEntry.toRoute()
                        LaunchedEffect(pollResult) {
                            pollResultViewModel.initializePollResult(
                                context = context,
                                pollId = pollResult.id,
                            )
                        }

                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        if (pollResultViewState.poll != null) {
                            ResultScreen(
                                modifier = Modifier,
                                state = pollResultViewState,
                                onFinish = pollResultViewModel::onFinish,
                            )
                        }
                    }
                    composable<Screens.Settings> {
                        val settingsViewModel: SettingsViewModel by viewModel()
                        val settingsViewState by settingsViewModel.settingsViewState.collectAsState()

                        SideEffect { settingsViewModel.initialize() }

                        SettingsScreen(
                            modifier = Modifier,
                            navController = navController,
                            settingsState = settingsViewState,
                            onShowOnboardingChange = settingsViewModel::updateShowOnboarding,
                            onPlaySoundChange = settingsViewModel::updatePlaySound,
                            onPinScreenChange = settingsViewModel::updatePinScreen,
                            onDefaultGradingSelected = settingsViewModel::updateDefaultGrading,
                            onDismissFeedback = {},
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