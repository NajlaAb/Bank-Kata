package com.bank.katabank.service.stepdefinitions;

import com.bank.katabank.dto.AccountStatementRequest;
import com.bank.katabank.dto.AccountStatementResponse;
import com.bank.katabank.dto.DepositRequest;
import com.bank.katabank.dto.WithdrawRequest;
import com.bank.katabank.exception.InsufficientBalanceException;
import com.bank.katabank.exception.InvalidInputException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class GetAccountStatementStep extends RootStep {

    List<AccountStatementResponse> response;

    @Given("User makes a deposit of {int} on the current day")
    public void user_makes_a_deposit_of_on_the_current_day(Integer int1) {
        try {
            DepositRequest request = DepositRequest.builder().accountId(1).amount(1200).build();
            accountService.deposit(request);
        } catch (InvalidInputException ignored) {
        }
    }

    @Given("User makes a withdrawal of {int} on the current day")
    public void user_makes_a_withdrawal_of_on_the_current_day(Integer int1) {
        WithdrawRequest request = WithdrawRequest.builder().accountId(1).amount(200).build();
        try {
            accountService.withdraw(request);
        } catch (InvalidInputException | InsufficientBalanceException ignored) {
        }
    }

    @When("User try to show his history")
    public void user_try_to_show_his_history() {
        AccountStatementRequest request = AccountStatementRequest.builder()
                .accountId(1)
                .build();
        response = accountService.getAccountStatement(request);
    }

    @Then("User would see")
    public void user_would_see(io.cucumber.datatable.DataTable dataTable) {
        Assertions.assertEquals(2, accountService.getAccountList().get(0).getStatements().size());
        Assertions.assertEquals(1000, accountService.getAccountList().get(0).getStatements().get(1).getBalance());
    }

}
