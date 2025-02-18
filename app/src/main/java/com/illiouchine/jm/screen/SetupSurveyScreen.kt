package com.illiouchine.jm.screen

import android.icu.util.Calendar
import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.illiouchine.jm.R
import com.illiouchine.jm.model.Survey
import com.illiouchine.jm.ui.theme.JmTheme
import java.text.DateFormat


@Composable
fun SetupSurveyScreen(
    modifier: Modifier = Modifier,
    setupFinished: (Survey) -> Unit = {},
) {

    val context = LocalContext.current
    var subject: String by remember { mutableStateOf("") }
    var proposal: String by remember { mutableStateOf("") }
    var proposals: List<String> by remember { mutableStateOf(emptyList()) }

    fun generateProposalName(): String {
        return "${context.getString(R.string.proposal)} ${(65 + proposals.size).toChar()}"
    }

    // TODO: Perhaps use the 'fun' def instead here ?
    val addProposal: () -> Unit = {
        // Rule: if the proposition name is not specified, use a default
        if (proposal == "") {
            proposal = generateProposalName()
        }
        proposals = proposals + proposal
        proposal = ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(state = ScrollState(initial = 0))
    ) {

        Text(stringResource(R.string.label_poll_subject))
        TextField(
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5,
            value = subject,
            onValueChange = { subject = it },
        )

        Text(stringResource(R.string.label_poll_proposals))
        Row {
            TextField(
//                modifier = Modifier.fillMaxWidth(), // "Add" button disappears
                singleLine = true,
                value = proposal,
                onValueChange = { proposal = it },
                placeholder = { Text(generateProposalName()) },
                keyboardActions = KeyboardActions(onDone = { addProposal() }),
            )
            Button(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                onClick = { addProposal() },
            ) { Text(stringResource(R.string.button_add)) }
        }

        proposals.forEach {
            Row {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(8.dp),
                    text = it,
                )
                Button(
                    onClick = { proposals = proposals - it },
                ) { Text("x") }
            }
        }

        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            enabled = proposals.size > 1,
            onClick = {
                // Rule: if the poll's subject was not provided, use a default.
                if (subject == "") {
                    subject = "Scrutin du" + " " + DateFormat.getDateInstance()
                        .format(Calendar.getInstance().time)
                }
                // Rule: if no proposals were added, abort and complain.
                // Note: since the button is now disabled in that case, this never happens anymore.
                if (proposals.size < 2) {
                    Toast.makeText(
                        context,
                        "A poll needs at least two proposals.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@Button
                }
                val survey = Survey(subject = subject, props = proposals)
                setupFinished(survey)
            },
        ) {
            Text(stringResource(R.string.button_let_s_go))
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun PreviewSetupSurveyScreen(modifier: Modifier = Modifier) {
    JmTheme {
        SetupSurveyScreen()
    }
}