Feature: Changing log levels while the application runs

  Scenario: An administrator turns on debug logging for one package
    Given an administrator called "ada"
    And "ada" has signed in
    When I set the log level of "uk.co.yottr.controller" to "DEBUG"
    Then the log level of "uk.co.yottr.controller" is "DEBUG"
    When I reset the log level of "uk.co.yottr.controller"
    Then "uk.co.yottr.controller" has no log level of its own
