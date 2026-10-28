Feature: Managing members

  Scenario: An administrator finds a member
    Given a member called "alice"
    And a member called "bob"
    And an administrator called "ada"
    And "ada" has signed in
    When I search the members for "alice"'s username
    Then the members found are "alice"

  Scenario: An administrator corrects a member's details
    Given a member called "alice"
    And an administrator called "ada"
    And "ada" has signed in
    When I change "alice"'s last name to "Fairweather"
    Then I see "Saved the details of"
    When I search the members for "alice"'s username
    Then I see "Izzy Fairweather"
