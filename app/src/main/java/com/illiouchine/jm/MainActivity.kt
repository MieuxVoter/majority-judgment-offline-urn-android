package com.illiouchine.jm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            var showOnboarding:Boolean by remember { mutableStateOf(true) }
            var currentSurvey: Survey? by remember { mutableStateOf(null) }
            var surveyResult: SurveyResult? by remember { mutableStateOf(null) }

            JmTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (showOnboarding) {
                        OnBoardingScreen(
                            modifier = Modifier.padding(innerPadding),
                            onFinish = { showOnboarding = false }
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