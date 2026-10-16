package com.illiouchine.jm.ui.screen.help

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.illiouchine.jm.R
import com.illiouchine.jm.ui.composable.spacer.MediumVerticalSpacer
import com.illiouchine.jm.ui.composable.title.ScreenTitle
import com.illiouchine.jm.ui.theme.JmTheme
import com.illiouchine.jm.ui.theme.Theme
import com.illiouchine.jm.ui.theme.spacing
import kotlinx.coroutines.launch

@Composable
fun GenericHelpScreen(
    modifier: Modifier = Modifier,
    title: String = "",
    testTag: String = "", // TBD: we can pass this using the modifier instead
    onNavigateBack: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .let {
                if (testTag.isNotBlank()) {
                    it.testTag(testTag)
                } else {
                    it
                }
            },
    ) { innerPadding ->

        val scrollState = rememberScrollState()
        val coroutineScope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(paddingValues = innerPadding)
                .padding(horizontal = Theme.spacing.small)
        ) {
            if (title.isNotBlank()) {
                ScreenTitle(
                    text = title,
                )
            }

            content()

            MediumVerticalSpacer()

            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    coroutineScope.launch {
                        onNavigateBack()
                    }
                },
            ) {
                Text(stringResource(R.string.button_back))
            }

            Spacer(Modifier.padding(vertical = Theme.spacing.large))
        }
    }
}

@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun PreviewGenericHelpScreen(modifier: Modifier = Modifier) {
    JmTheme {
        GenericHelpScreen(
            title = "The Test Help",
            modifier = modifier,
        ) {
            Text(
                """
                Do reprehenderit pariatur reprehenderit bell. Soup tempor incididunt fugiat spinach et soba esse esse chia soba corn natural irure. Pariatur sanctuary shallot squash et consequat support sint soba est legumes sustain sincerity humility.

                Occaecat esse leek plant-based integrity bowl excepteur fiber do grain. Herbivore oatmilk kale life legumes amet adipisicing peas lettuce selflessness officia spinach love radish. Sweet shallot mercy cupidatat lentils natural quinoa mustard fruit mint magna herbivore nonviolence veniam exercitation.

                Peas avocado wholefood exercitation cabbage sympathy positivity warmth peas. Veniam ullamco empathy commodo radicchio love. Pepper helpfulness noodles pepper positivity sint leek truth avocado avocado tomato duis. Beet plant peas jackfruit bowl amet plant-based voluptate integrity rights mercy patience. Lorem sweet lentils magna peace aliquip nut tofu positivity compassion. Adipisicing grain green grace minim bowl chickpea curry.
                """.trimIndent()
            )
        }
    }
}
