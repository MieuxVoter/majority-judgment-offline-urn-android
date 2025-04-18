package com.illiouchine.jm.test

import android.app.Activity
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import io.cucumber.java.After

class ActivityScenarioHolder {

    private var scenario: ActivityScenario<*>? = null

    fun launch(intent: Intent) {
        scenario = ActivityScenario.launch<Activity>(intent)
    }

    /**
     * Close the activity after the scenario.
     */
    @After
    fun close() {
        scenario?.close()
    }

}
