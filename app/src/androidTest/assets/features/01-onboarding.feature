Feature: Onboarding
  In order to boostrap my usage of the app
  As a user
  I want a quick and clear onboarding

  Scenario: Skim the onboarding
    Given I launch the app
    And I wait for idle

    Then I should not see the home screen
    Then I should see the onboarding screen
    Then I should see the node tagged "onboarding_screen_next"

    When I click on the node tagged "onboarding_screen_next"
    Then I should see the onboarding screen
    Then I should see the node tagged "onboarding_screen_next"

    When I click on the node tagged "onboarding_screen_next"
    Then I should see the onboarding screen
    Then I should see the node tagged "onboarding_screen_finish"

    When I click on the node tagged "onboarding_screen_finish"
    Then I should not see the onboarding screen
    But I should see the home screen
