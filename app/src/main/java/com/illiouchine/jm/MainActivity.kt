package com.illiouchine.jm

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.illiouchine.jm.ui.composable.MUSnackbar
import com.illiouchine.jm.ui.screen.OnBoardingScreen
import com.illiouchine.jm.ui.screen.ResultScreen
import com.illiouchine.jm.ui.screen.SetupSurveyScreen
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

            // Rule: the screen should never lock during the poll
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            JmTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        MUSnackbar(
                            modifier = Modifier,
                            text = viewState.feedback,
                            onDismiss = {
                                viewModel.onDismissFeedback()
                            },
                        )
                    },
                    // TODO: figure out the weird gap "bug" that this generates
//                    bottomBar = {
//                        MUBottomBar(
//                            modifier = Modifier,
//                            selected = navController.currentDestination?.route ?: "home",
//                            onItemSelected = { destination -> navController.navigate(destination.id) }
//                        )
//                    },
                ) { innerPadding ->
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {

                            if (viewState.showOnboarding) {
                                OnBoardingScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    onFinish = { viewModel.onFinishOnBoarding() },
                                )
                            } else {
                                if (viewState.currentPoll == null) {
                                    SetupSurveyScreen(
                                        modifier = Modifier.padding(innerPadding),
                                        pollSetup = viewState.pollSetup,
                                        onAddSubject = { viewModel.onAddSubject(it) },
                                        onAddProposal = { viewModel.onAddProposal(it) },
                                        onRemoveProposal = { viewModel.onRemoveProposal(it) },
                                        setupFinished = { viewModel.onFinishSetupSurvey() },
                                    )
                                } else {
                                    if (viewState.pollResult == null) {
                                        VotingScreen(
                                            modifier = Modifier.padding(innerPadding),
                                            poll = viewState.currentPoll!!,
                                            onFinish = { viewModel.onFinishVoting(it) },
                                        )
                                    } else {
                                        ResultScreen(
                                            modifier = Modifier.padding(innerPadding),
                                            pollResult = viewState.pollResult!!,
                                            onFinish = {
                                                viewModel.onResetState()
                                            },
                                        )
                                    }
                                }
                            }
                        }
                        composable("setup") {

                        }
                        composable("vote") {

                        }
                        composable("result") {

                        }
                        composable("settings") {
                            Text(
                                modifier = Modifier.padding(innerPadding),
                                text = "Settings",
                            )
                        }
                    }

                }
            }
        }
    }
}