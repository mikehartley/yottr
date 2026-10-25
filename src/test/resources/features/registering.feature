Feature: Registering

  Scenario: A visitor registers and can then sign in
    When I register as "carol"
    Then I see "welcome to Yottr"
    When "carol" signs in
    Then I see "Logout"
