Feature: Signing in and out

  Scenario: Visitors are offered the sign-in form
    When I open the home page
    Then I see "Sign in"

  Scenario: A wrong password is rejected
    Given a member called "alice"
    When "alice" signs in with the password "not-the-password"
    Then I see "Invalid username and password."

  Scenario: A member signs in and out
    Given a member called "alice"
    When "alice" signs in
    Then I see "Sign out"
    When I sign out
    Then I see "You have signed out."
