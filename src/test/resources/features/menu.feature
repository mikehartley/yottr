Feature: The menu shows where you are

  Scenario: The menu marks the page a member is on
    Given a member called "alice"
    And "alice" has signed in
    And I have posted a listing for a "Sigma 33"
    When I open "/s/listings/all"
    Then the menu shows I am in "Boats"
    When I open the "Sigma 33" listing from all the boats
    Then the menu shows I am in "Boats"
    When I open "/s/listings/mine"
    Then the menu shows I am in "My Listings"
    When I open "/s/myDetails"
    Then the menu shows I am in "My Details"
