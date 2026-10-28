Feature: Managing members

  Scenario: An administrator finds a member
    Given a member called "alice"
    And a member called "bob"
    And an administrator called "ada"
    And "ada" has signed in
    When I search the members for "alice"'s username
    Then the members found are "alice"
