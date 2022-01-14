Feature: In order to retrieve some or all of my savings
  As a bank client
  I want to make a withdrawal from my account

  Scenario: Enter negative amount when withdrawing money
    Given User has certain balance in his account
    When User withdraw negative amount
    Then No statement is added to his account

  Scenario: Enter positive amount when withdrawing money, insufficient balance
    Given User has certain balance in his account
    When User withdraw an amount greater than the current balance
    Then No statement is added to his account

  Scenario: Enter positive amount when withdrawing money, enough balance
    Given User has certain balance in his account
    When User withdraw an amount lower than the current balance
    Then new statement is added to his account with the value of the current balance get decreased by the given amount