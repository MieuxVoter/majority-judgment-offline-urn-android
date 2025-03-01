Feature: Onboarding
  In order to boostrap my usage of the app
  As a user
  I want a quick and clear onboarding

  Scenario: Skim the onboarding
    Given I launch the app
    Then I should see the node tagged "screen_onboarding"
    Then I should see the node tagged "screen_onboarding_next"
