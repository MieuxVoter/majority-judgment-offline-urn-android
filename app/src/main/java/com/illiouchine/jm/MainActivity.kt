package com.illiouchine.jm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.screen.OnBoardingScreen
import com.illiouchine.jm.screen.ResultScreen
import com.illiouchine.jm.screen.SetupSurveyScreen
import com.illiouchine.jm.screen.VotingScreen
import com.illiouchine.jm.ui.theme.JmTheme
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val viewState by viewModel.viewState.collectAsState()

            JmTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        if(!viewState.feedback.isNullOrEmpty()){
                            LaunchedEffect(viewState.feedback) {
                                delay(5*1000)
                                viewModel.onDismissFeedback()
                            }
                            Snackbar(
                                modifier = Modifier.padding(16.dp)
                                    .padding(WindowInsets.ime.asPaddingValues()),
                                dismissAction = {
                                    Button(
                                        onClick = { viewModel.onDismissFeedback() },
                                    ) {
                                        Text("Dismiss")
                                    }
                                },

                            ) {
                                Text("${viewState.feedback}")
                            }
                        }
                    }
                ) { innerPadding ->
                    if (viewState.showOnboarding) {
                        OnBoardingScreen(
                            modifier = Modifier.padding(innerPadding),
                            onFinish = { viewModel.onFinishOnBoarding() }
                        )
                    } else {
                        if (viewState.currentSurvey == null) {
                            SetupSurveyScreen(
                                modifier = Modifier.padding(innerPadding),
                                setupSurvey = viewState.setupSurvey,
                                onAddSubject = { viewModel.onAddSubject(it) },
                                onAddProposal = { viewModel.onAddProposals(it) },
                                onRemoveProposal = { viewModel.onRemoveProposal(it) },
                                setupFinished = { viewModel.onFinishSetupSurvey() }
                            )
                        } else {
                            if (viewState.surveyResult == null) {
                                VotingScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    survey = viewState.currentSurvey!!,
                                    onFinish = { viewModel.onFinishVoting(it) }
                                )
                            } else {
                                ResultScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    surveyResult = viewState.surveyResult!!,
                                    onFinish = {
                                        viewModel.onResetState()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}