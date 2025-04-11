package com.illiouchine.jm.test

import io.cucumber.java.en.Given
import io.cucumber.java.en.Then

class DummySteps {

    @Given("^I do nothing$")
    fun doNothing() {}

    @Given("^I give the device to someone else$")
    fun giveDeviceToSomeoneElse() {}

    @Then("^another participant becomes the \"I\" in our narration$")
    fun anotherParticipantBecomesI() {}

}