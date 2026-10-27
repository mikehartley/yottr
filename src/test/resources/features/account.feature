Feature: Closing an account

  Scenario: A member deletes their account, listings and all
    Given a member called "alice"
    And "alice" has signed in
    And I have posted a listing for a "Sigma 33"
    When I delete my account
    Then I see "You have signed out."
    When "alice" signs in
    Then I see "Invalid username and password."
