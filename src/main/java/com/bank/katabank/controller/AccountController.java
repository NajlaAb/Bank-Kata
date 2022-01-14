package com.bank.katabank.controller;

import com.bank.katabank.dto.AccountStatementRequest;
import com.bank.katabank.dto.AccountStatementResponse;
import com.bank.katabank.dto.DepositRequest;
import com.bank.katabank.dto.WithdrawRequest;
import com.bank.katabank.service.AccountService;
import com.bank.katabank.utils.MessageUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(MessageUtils.ACCOUNT)
public class AccountController {

    /**
     * Account Service
     */
    @Autowired
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @ApiOperation(value = "Deposit amount of money. The amount should be positive")
    @PostMapping(MessageUtils.DEPOSIT)
    ResponseEntity<?> deposit(@Valid @RequestBody DepositRequest request) {
        accountService.deposit(request);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Withdraw amount of money. The amount should be positive and the current balance should be sufficient")
    @PostMapping(MessageUtils.WITHDRAW)
    ResponseEntity<?> withdraw(@Valid @RequestBody WithdrawRequest request) {
        accountService.withdraw(request);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "View Account Statement By Year, Month or by a Beginning Date and an Ending Date. If the period type is not provided, return all successful transactions")
    @PostMapping(MessageUtils.STATEMENT)
    ResponseEntity<List<AccountStatementResponse>> getAccountStatement(@Valid @RequestBody AccountStatementRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(accountService.getAccountStatement(request));
    }
}
