package com.bank.katabank.service.stepdefinitions;

import com.bank.katabank.dto.DepositRequest;
import com.bank.katabank.exception.InvalidInputException;
import com.bank.katabank.model.Account;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;

public class DepositSteps extends RootStep {

    @Given("User has an empty account")
    public void user_has_an_empty_account() {
        accountService.getAccountList().clear();
        accountService.getAccountList().add(Account.builder()
                .id(1)
                .currentBalance(0)
                .statements(new ArrayList<>())
                .build());
        statementListSize = accountService.getAccountList().get(0).getStatements().size();
    }

    @When("User deposit negative amount")
    public void user_deposit_negative_amount() {
        DepositRequest request = DepositRequest.builder().accountId(1).amount(-1200).build();
        try {
            accountService.deposit(request);
        } catch (InvalidInputException ignored) {
        }
    }

    @Then("No statement should be added to his account")
    public void no_statement_should_be_added_to_his_account() {
        Assertions.assertEquals(accountListSize, accountService.getAccountList().get(0).getStatements().size());
    }

    @Given("User has no account")
    public void user_has_no_account() {
        accountService.getAccountList().clear();
        accountListSize = accountService.getAccountList().size();
    }

    @When("User deposit positive amount")
    public void user_deposit_positive_amount() {
        try {
            DepositRequest request = DepositRequest.builder().accountId(1).amount(1200).build();
            accountService.deposit(request);
        } catch (InvalidInputException ignored) {
        }
    }

    @Then("New Account is added to the account list")
    public void new_account_is_added_to_the_account_list() {
        Assertions.assertEquals(++accountListSize, accountService.getAccountList().size());
    }

    @Then("New statement is added to his account with the value of the current balance get increased by the given amount")
    public void new_statement_is_added_to_his_account_with_the_value_of_the_current_balance_get_increased_by_the_given_amount() {
        Assertions.assertEquals(++statementListSize, accountService.getAccountList().get(0).getStatements().size());
        Assertions.assertEquals(1200, accountService.getAccountList().get(0).getStatements().get(0).getBalance());
    }
}
