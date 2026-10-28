Feature: Listing a boat

  Scenario: A member posts a listing
    Given a member called "alice"
    And "alice" has signed in
    When I post a listing for a "Sigma 33"
    Then I see "Boat Saved Successfully."
    And my listings include a "Sigma 33"

  Scenario: A member edits their listing
    Given a member called "alice"
    And "alice" has signed in
    And I have posted a listing for a "Sigma 33"
    When I change the manufacturer of my "Sigma 33" listing to "Sigma 38"
    Then my listings include a "Sigma 38"
    And my listings do not include a "Sigma 33"

  Scenario: A member at their listing limit cannot add another
    Given a member called "dave" who may have 0 listings
    And "dave" has signed in
    When I open "/s/listings/new"
    Then I see "You have reached the limit"

  Scenario: A member deletes a listing
    Given a member called "alice"
    And "alice" has signed in
    And I have posted a listing for a "Sigma 33"
    And I have posted a listing for a "Contessa 32"
    When I delete my "Sigma 33" listing
    Then my listings do not include a "Sigma 33"
    And my listings include a "Contessa 32"

  Scenario: A member sees who to get in touch with about a boat
    Given a member called "alice"
    And a member called "dave"
    And "alice" has signed in
    And I have posted a listing for a "Sigma 33"
    And I sign out
    When "dave" signs in
    And I open the "Sigma 33" listing from all the boats
    Then I see "Get in touch"
    And I see "alice@example.test"

  Scenario: A member searches the boats
    Given a member called "alice"
    And a member called "dave"
    And "alice" has signed in
    And I have posted a listing for a "Moody 31"
    And I have posted a listing for a "Westerly Konsort"
    And I sign out
    When "dave" signs in
    And I search the boats for "moody"
    Then the boats found include a "Moody 31"
    And the boats found do not include a "Westerly Konsort"

  Scenario: An administrator withdraws a listing, which its owner can't put back
    Given a member called "alice"
    And a member called "dave"
    And an administrator called "ada"
    And "alice" has signed in
    And I have posted a listing for a "Hallberg-Rassy 42"
    And I sign out
    When "ada" signs in
    And I open the "Hallberg-Rassy 42" listing from all the boats
    And I withdraw the listing as an administrator
    Then I see "Withdrawn by an administrator"
    And I sign out
    When "dave" signs in
    And I search the boats for "Hallberg-Rassy 42"
    Then the boats found do not include a "Hallberg-Rassy 42"
    And I sign out
    When "alice" signs in
    And I open "/s/listings/mine"
    Then I see "Withdrawn by an administrator"

  Scenario: An administrator deletes a listing
    Given a member called "alice"
    And an administrator called "ada"
    And "alice" has signed in
    And I have posted a listing for a "Contessa 26"
    And I sign out
    When "ada" signs in
    And I open the "Contessa 26" listing from all the boats
    And I delete the listing as an administrator
    Then I see "Deleted the listing"
    And the boats found do not include a "Contessa 26"

  Scenario: A member searches the boats by length, in feet whatever the listing uses
    Given a member called "alice"
    And a member called "dave"
    And "alice" has signed in
    And I have posted a listing for a "Oyster 56" of 17 m
    And I have posted a listing for a "Etap 21" of 21 ft
    And I sign out
    When "dave" signs in
    And I search the boats for boats of 50 ft or more
    Then the boats found include a "Oyster 56"
    And the boats found do not include a "Etap 21"
