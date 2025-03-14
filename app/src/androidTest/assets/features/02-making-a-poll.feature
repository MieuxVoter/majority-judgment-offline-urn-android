Feature: Making a Poll
  In order to decide about something
  As a user
  I want to set up a poll and use it

  Scenario: Set up a poll and use it
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

     # Submit button should now be enabled (2 proposals)
     When I click on the node tagged "setup_add_proposal"
     # But we might need to scroll to it first, on small screens
     When I scroll to the node tagged "setup_submit"
      And I click on the node tagged "setup_submit"
     Then I should not see the setup screen anymore

      And I wait for idle
      And I wait for 1s

     Then I should see the node tagged "voting_start_first"
     Then I should not see the node tagged "voting_start_next"

     When I click on the node tagged "voting_start_first"
      And I wait for idle

     # Notice how not waiting for idle here does not yield the same behavior as when we do.
     When I click on the node tagged "grade_selection_4"
      And I wait for 1s
     When I click on the node tagged "grade_selection_2"
      And I wait for 1s

     Then I should see the node tagged "ballot_summary"
     Then I should see the node tagged "ballot_summary_rescind"
     Then I should see the node tagged "ballot_summary_confirm"

     When I click on the node tagged "ballot_summary_rescind"
      And I wait for idle
      And I wait for 1s

     Then I should see the node tagged "voting_start_first"
     When I click on the node tagged "voting_start_first"
      And I wait for idle

     When I click on the node tagged "grade_selection_0"
      And I wait for idle
      And I wait for 1s
     When I click on the node tagged "grade_selection_1"
      And I wait for idle
      And I wait for 1s

     Then I should see the node tagged "ballot_summary"
     When I click on the node tagged "ballot_summary_confirm"
      And I wait for idle
      And I wait for 1s

     Then I should not see the node tagged "voting_start_first"
      But I should see the node tagged "voting_start_next"

        # Second user

     Then I become another participant

     When I click on the node tagged "voting_start_next"
      And I wait for idle

     When I click on the node tagged "grade_selection_2"
      And I wait for idle
      And I wait for 1s
     When I click on the node tagged "grade_selection_3"
      And I wait for idle
      And I wait for 1s

     Then I should see the node tagged "ballot_summary"
     When I click on the node tagged "ballot_summary_confirm"
      And I wait for idle
      And I wait for 1s

 #    Then there should be 2 ballots in the urn





