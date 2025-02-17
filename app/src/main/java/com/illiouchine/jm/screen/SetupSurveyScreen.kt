package com.illiouchine.jm.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.model.Survey
import com.illiouchine.jm.ui.theme.JmTheme


@Composable
fun SetupSurveyScreen(
    modifier: Modifier = Modifier,
    setupFinished: (Survey) -> Unit = {},
) {

    var subject: String by remember { mutableStateOf("") }
    var proposition: String by remember { mutableStateOf("") }
    var props: List<String> by remember { mutableStateOf(emptyList()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
//        Text("SetupSurveyScreen")

        Text("Sujet du Scrutin")
        TextField(
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5,
            value = subject,
            onValueChange = { subject = it },
        )

        Text("Propositions")
        Row {
            TextField(
//                modifier = Modifier.fillMaxWidth(), // "Add" button disappears
//                placeholder = ???,
                singleLine = true,
                value = proposition,
                onValueChange = { proposition = it },
            )
            Button(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                onClick = {
                    // Rule: if the proposition name is not specified, use a default
                    if (proposition == "") {
                        proposition = "Proposition" + " " + (65+props.size).toChar()
                    }
                    val newProps = proposition
                    props = props + newProps
                    proposition = ""
                },
            ) { Text("Add") }
        }

        props.forEach {
            Row {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(8.dp),
                    text = it,
                )
                Button(
                    onClick = { props = props - it },
                ) { Text("x") }
            }
        }

        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            onClick = {
                val survey = Survey(asking = subject, props = props)
                setupFinished(survey)
            },
        ) {
            Text("C'est parti !")
        }
    }
}


@Preview
@Composable
fun PreviewSetupSurveyScreen(modifier: Modifier = Modifier) {
    JmTheme {
        SetupSurveyScreen()
    }
}