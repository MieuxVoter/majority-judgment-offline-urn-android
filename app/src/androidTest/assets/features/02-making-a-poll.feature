Feature: Making a Poll
  In order to decide about something
  As a user
  I want to set up a poll

  Scenario: Set up a poll
    Given I launch the app
    And I wait for idle

    Then I should see the home screen
    Then I should not see the node tagged "onboarding_screen"
    Then I should see the node tagged "home_fab"
    When I click on the node tagged "home_fab"
    Then I should not see the home screen anymore
    But I should now see the setup screen
    Then I should see the node tagged "setup_add_proposal"
    Then I should see the node tagged "setup_submit"

    # Submit button should be disabled (0 proposal)
    When I click on the node tagged "setup_submit"
    Then I should still see the setup screen

    # Submit button should be disabled (1 proposal)
    When I click on the node tagged "setup_add_proposal"
    When I click on the node tagged "setup_submit"
    Then I should still see the setup screen

    # Submit button should be enabled (2 proposals)
    When I click on the node tagged "setup_add_proposal"
    When I click on the node tagged "setup_submit"
    Then I should not see the setup screen anymore

