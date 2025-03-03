Feature: Making a Poll
  In order to decide about something
  As a user
  I want to set up a poll

  Scenario: Set up a poll
    Given I launch the app
    And I wait for idle

    Then I should see the node tagged "screen_home"
    Then I should not see the node tagged "screen_onboarding"
    Then I should see the node tagged "home_fab"
    When I click on the node tagged "home_fab"
    Then I should see the node tagged "screen_setup"
    Then I should see the node tagged "setup_add_proposal"
    Then I should see the node tagged "setup_submit"

    # Submit button should be disabled (0 proposal)
    When I click on the node tagged "setup_submit"
    Then I should see the node tagged "screen_setup"

    # Submit button should be disabled (1 proposal)
    When I click on the node tagged "setup_add_proposal"
    When I click on the node tagged "setup_submit"
    Then I should see the node tagged "screen_setup"

    # Submit button should be enabled (2 proposals)
    When I click on the node tagged "setup_add_proposal"
    When I click on the node tagged "setup_submit"
    Then I should not see the node tagged "screen_setup"

