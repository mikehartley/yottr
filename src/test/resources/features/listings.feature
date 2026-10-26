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
