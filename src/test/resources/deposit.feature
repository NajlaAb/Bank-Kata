Feature: In order to save money
  As a bank client
  I want to make a deposit in my account

  Scenario: Enter negative amount when saving money
    Given User has an empty account
    When User deposit negative amount
    Then No statement should be added to his account

  Scenario: Enter positive amount when saving money
    Given User has no account
    When User deposit positive amount
    Then New Account is added to the account list

  Scenario: Enter positive amount when saving money
    Given User has an empty account
    When User deposit positive amount
    Then New statement is added to his account with the value of the current balance get increased by the given amount