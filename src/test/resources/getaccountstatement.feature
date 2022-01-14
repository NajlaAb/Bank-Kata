Feature: In order to check my operations
  As a bank client
  I want to see the history (operation, date, amount, balance) of my operations

  Scenario: show history
    Given User makes a deposit of 2000 on the current day
    And User makes a withdrawal of 1600 on the current day
    When User try to show his history
    Then User would see
      |date       | credit     | debit      | balance
      |01-11-2021 | 2000       |            | 2000
      |16-11-2021 |            | 1600       | 400