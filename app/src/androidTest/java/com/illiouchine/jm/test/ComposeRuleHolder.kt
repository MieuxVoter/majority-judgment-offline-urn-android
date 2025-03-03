package com.illiouchine.jm.test

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.illiouchine.jm.MainActivity
import io.cucumber.junit.WithJunitRule
import org.junit.Rule
import javax.inject.Inject
import javax.inject.Singleton

// We're probably going to need a rule holder if we want multiple step files.

@WithJunitRule
@Singleton
class ComposeRuleHolder @Inject constructor() {
    @get:Rule(order = 1)
    val rule = createAndroidComposeRule<MainActivity>()
}
