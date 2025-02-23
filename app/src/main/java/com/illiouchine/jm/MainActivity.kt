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
import com.illiouchine.jm.model.PollConfig
import com.illiouchine.jm.ui.screen.HomeScreen
import com.illiouchine.jm.ui.screen.OnBoardingScreen
import com.illiouchine.jm.ui.screen.PollSetupScreen
import com.illiouchine.jm.ui.screen.PollVotingScreen
import com.illiouchine.jm.ui.screen.ResultScreen
import com.illiouchine.jm.ui.screen.SettingsScreen
import com.illiouchine.jm.ui.theme.JmTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel()
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val viewState by viewModel.viewState.collectAsState()
            val settingsState by settingsViewModel.settingsViewState.collectAsState()

            val navController = rememberNavController()

            // TODO: don't prevent lock at all times ; only during vote (and perhaps results)
            // Rule: the screen should never lock during the poll
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
                                onSetupBlankPoll = {
                                    viewModel.onStartPollSetup(PollConfig())
                                    navController.navigate(Screens.PollSetup.name)
                                },
                            )
                        }
                    }
                    composable(Screens.PollSetup.name) {
                        PollSetupScreen(
                            modifier = Modifier,
                            navController = navController,
                            pollSetup = viewState.pollSetup,
                            onAddSubject = { viewModel.onAddSubject(it) },
                            onAddProposal = { viewModel.onAddProposal(it) },
                            onRemoveProposal = { viewModel.onRemoveProposal(it) },
                            setupFinished = {
                                viewModel.onFinishPollSetup()
                                navController.navigate(Screens.PollVote.name)
                            },
                            feedback = viewState.feedback,
                            onDismissFeedback = { viewModel.onDismissFeedback() },
                        )
                    }
                    composable(Screens.PollVote.name) {
                        PollVotingScreen(
                            modifier = Modifier,
                            pollConfig = viewState.currentPollConfig!!,
                            onFinish = {
                                viewModel.onFinishVoting(it)
                                navController.navigate(Screens.PollResult.name)
                            },
                        )
                    }
                    composable(Screens.PollResult.name) {
                        if (viewState.pollResult != null) {
                            ResultScreen(
                                modifier = Modifier,
                                poll = viewState.pollResult!!,
                                onFinish = {
                                    viewModel.onResetState()
                                    navController.navigate(Screens.Home.name)
                                },
                            )
                        } else {
                            // During the screen navigation transition to home (in onFinish above),
                            // we end up here very briefly, 'cause concurrency.
                            // FIXME: How should we handle this ?  A Loading Spinner Composable ?
                            // For now, a blank slate is OK-ish I guess ?
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
                                viewModel.onDismissFeedback()
                            },
                        )
                    }
                }
            }
        }
    }
}