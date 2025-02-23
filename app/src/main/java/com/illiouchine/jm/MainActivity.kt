package com.illiouchine.jm

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.illiouchine.jm.model.Poll
import com.illiouchine.jm.ui.screen.OnBoardingScreen
import com.illiouchine.jm.ui.screen.PollSetupScreen
import com.illiouchine.jm.ui.screen.ResultScreen
import com.illiouchine.jm.ui.screen.SettingsScreen
import com.illiouchine.jm.ui.screen.SettingsScreenState
import com.illiouchine.jm.ui.screen.VotingScreen
import com.illiouchine.jm.ui.theme.JmTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewState by viewModel.viewState.collectAsState()
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
                        if (viewState.showOnboarding) {
                            OnBoardingScreen(
                                modifier = Modifier,
                                onFinish = { viewModel.onFinishOnBoarding() },
                            )
                        } else {
                            // TODO: home screen layout
                            // TODO: position this FAB
                            FloatingActionButton(
                                modifier = Modifier.padding(64.dp),
                                onClick = {
                                    viewModel.onStartPollSetup(Poll())
                                    navController.navigate(Screens.PollSetup.name)
                                }
                            ) {
                                // FIXME: icon of an urn and a plus sign ?
                                Text("Go")
                            }
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
                        VotingScreen(
                            modifier = Modifier,
                            poll = viewState.currentPoll!!,
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
                                pollResult = viewState.pollResult!!,
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
                            settingsScreenState = SettingsScreenState(),
                            onShowOnboardingChange = {},
                            onDismissFeedback = {}
                        )
                    }
                }
            }
        }
    }
}