Feature: Access to members' and administrators' pages

  Scenario: Visitors are asked to sign in before seeing their listings
    When I open "/s/listings/mine"
    Then I am on the sign-in page

  Scenario: Members cannot open the administrators' pages
    Given a member called "alice"
    And "alice" has signed in
    When I open "/admin/users"
    Then access is refused

  Scenario: Members cannot change log levels
    Given a member called "alice"
    And "alice" has signed in
    When I open "/admin/logging"
    Then access is refused
