package com.bank.katabank.service.stepdefinitions;

import com.bank.katabank.dto.WithdrawRequest;
import com.bank.katabank.exception.InsufficientBalanceException;
import com.bank.katabank.exception.InvalidInputException;
import com.bank.katabank.model.Account;
import com.bank.katabank.model.Statement;
import com.bank.katabank.utils.MessageUtils;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WithdrawStep extends RootStep {

    @Given("User has certain balance in his account")
    public void user_has_certain_balance_in_his_account() {
        List<Statement> statements = new ArrayList<>();
        statements.add(new Statement(
                new SimpleDateFormat(MessageUtils.DATE_PATTERN).format(new Date()), 1000, 1000
        ));
        accountService.getAccountList().add(Account.builder()
                .id(1)
                .currentBalance(1000)
                .statements(statements)
                .build());
        statementListSize = accountService.getAccountList().get(0).getStatements().size();
    }

    @When("User withdraw negative amount")
    public void user_withdraw_negative_amount() {
        WithdrawRequest request = WithdrawRequest.builder().accountId(1).amount(-1200).build();
        try {
            accountService.withdraw(request);
        } catch (InvalidInputException | InsufficientBalanceException ignored) {
        }
    }

    @Then("No statement is added to his account")
    public void no_statement_is_added_to_his_account() {
        Assertions.assertEquals(statementListSize, accountService.getAccountList().get(0).getStatements().size());
    }

    @When("User withdraw an amount greater than the current balance")
    public void user_withdraw_an_amount_greater_than_the_current_balance() {
        WithdrawRequest request = WithdrawRequest.builder().accountId(1).amount(1200).build();
        try {
            accountService.withdraw(request);
        } catch (InvalidInputException | InsufficientBalanceException ignored) {
        }
    }

    @When("User withdraw an amount lower than the current balance")
    public void user_withdraw_an_amount_lower_than_the_current_balance() {
        WithdrawRequest request = WithdrawRequest.builder().accountId(1).amount(200).build();
        try {
            accountService.withdraw(request);
        } catch (InvalidInputException | InsufficientBalanceException ignored) {
        }
    }

    @Then("new statement is added to his account with the value of the current balance get decreased by the given amount")
    public void new_statement_is_added_to_his_account_with_the_value_of_the_current_balance_get_decreased_by_the_given_amount() {
        Assertions.assertEquals(++statementListSize, accountService.getAccountList().get(0).getStatements().size());
        Assertions.assertEquals(800, accountService.getAccountList().get(0).getStatements().get(1).getBalance());
    }

}
