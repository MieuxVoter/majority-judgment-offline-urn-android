Feature: Onboarding
  In order to boostrap my usage of the app
  As a user
  I want a quick and clear onboarding

  Scenario: Skim the onboarding
    Given I launch the app
    And I wait for idle

    Then I should not see the node tagged "screen_home"
    Then I should see the node tagged "screen_onboarding"
    Then I should see the node tagged "screen_onboarding_next"

    When I click on the node tagged "screen_onboarding_next"
    Then I should see the node tagged "screen_onboarding"
    Then I should see the node tagged "screen_onboarding_next"

    When I click on the node tagged "screen_onboarding_next"
    Then I should see the node tagged "screen_onboarding"
    Then I should see the node tagged "screen_onboarding_finish"

    When I click on the node tagged "screen_onboarding_finish"
    Then I should not see the node tagged "screen_onboarding"
    But I should see the node tagged "screen_home"
