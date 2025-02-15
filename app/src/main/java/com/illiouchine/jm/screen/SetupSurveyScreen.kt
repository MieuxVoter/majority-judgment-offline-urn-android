package com.illiouchine.jm.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.model.Survey
import com.illiouchine.jm.ui.theme.JmTheme


@Composable
fun SetupSurveyScreen(
    modifier: Modifier = Modifier,
    setupFinished: (Survey) -> Unit = {}
) {

    var asking: String by remember { mutableStateOf("") }
    var proposition: String by remember { mutableStateOf("") }
    var props: List<String> by remember { mutableStateOf(emptyList()) }
    
    Column(modifier = modifier.fillMaxSize()
        .background(Color.White)
        .padding(16.dp)
    ) {
        Text("SetupSurveyScreen")
        Text("Votre question")
        TextField(value = asking, onValueChange = { asking = it })
        Text("Propositions")
        Row {
            TextField(value = proposition, onValueChange = { proposition = it })
            Button(onClick = {
                val newProps = proposition
                props = props + newProps
                proposition = ""
            }) { Text("Add") }
        }

        props.forEach {
            Row {
                Text(it)
                Button(
                    onClick = { props = props - it },
                ) { Text("X") }
            }
        }

        Button(onClick = {
            val survey = Survey(asking = asking, props = props)
            setupFinished(survey)
        }) { Text("Validate") }
    }
}

@Preview
@Composable
fun PreviewSetupSurveyScreen(modifier: Modifier = Modifier) {
    JmTheme {
        SetupSurveyScreen()
    }
}