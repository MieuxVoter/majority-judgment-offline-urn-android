package com.illiouchine.jm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.illiouchine.jm.screen.OnBoardingScreen
import com.illiouchine.jm.screen.SetupSurveyScreen
import com.illiouchine.jm.model.Survey
import com.illiouchine.jm.model.SurveyResult
import com.illiouchine.jm.screen.ResultScreen
import com.illiouchine.jm.screen.VotingScreen
import com.illiouchine.jm.ui.theme.JmTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val viewState by viewModel.viewState.collectAsState()

            var currentSurvey: Survey? by remember { mutableStateOf(null) }
            var surveyResult: SurveyResult? by remember { mutableStateOf(null) }

            JmTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (viewState.showOnboarding) {
                        OnBoardingScreen(
                            modifier = Modifier.padding(innerPadding),
                            onFinish = { viewModel.onFinishOnBoarding() }
                        )
                    } else {
                        if (currentSurvey == null){
                            SetupSurveyScreen(
                                modifier = Modifier.padding(innerPadding),
                                setupFinished = { currentSurvey = it }
                            )
                        } else {
                            if (surveyResult == null){
                                VotingScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    survey = currentSurvey!!,
                                    onFinish = { surveyResult = it }
                                )
                            } else {
                                ResultScreen(
                                    modifier = Modifier.padding(innerPadding),
                                    surveyResult = surveyResult!!,
                                    onFinish = {
                                        currentSurvey = null
                                        surveyResult = null
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