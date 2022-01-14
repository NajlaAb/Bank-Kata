package com.bank.katabank.controller;

import com.bank.katabank.dto.AccountStatementRequest;
import com.bank.katabank.dto.AccountStatementResponse;
import com.bank.katabank.dto.DepositRequest;
import com.bank.katabank.dto.WithdrawRequest;
import com.bank.katabank.model.Account;
import com.bank.katabank.model.Statement;
import com.bank.katabank.model.enumeratin.PeriodTypeEnum;
import com.bank.katabank.service.impl.AccountServiceImpl;
import com.bank.katabank.utils.MessageUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AccountControllerIntegrationTest {

    @Autowired
    private AccountController accountController;

    @Autowired
    private AccountServiceImpl accountService;

    @BeforeEach
    public void initEach(){
        accountService.getAccountList().clear();
    }

    @Test
    public void shouldDepositMoney() {
        DepositRequest request = DepositRequest.builder().accountId(1).amount(1200).build();

        accountController.deposit(request);

        Assertions.assertEquals(1, (long) accountService.getAccountList().size());
        Assertions.assertEquals(1, (long) accountService.getAccountList().get(0).getStatements().size());
        Assertions.assertEquals(1200, accountService.getAccountList().get(0).getCurrentBalance());
    }

    @Test
    public void shouldWithdrawMoney() {
        List<Statement> statements = new ArrayList<>();
        statements.add(new Statement(
                new SimpleDateFormat(MessageUtils.DATE_PATTERN).format(new Date()), 1000, 1000
        ));
        accountService.getAccountList().add(Account.builder()
                .id(1)
                .currentBalance(1000)
                .statements(statements)
                .build());

        WithdrawRequest request = WithdrawRequest.builder().accountId(1).amount(700).build();

        accountController.withdraw(request);

        Assertions.assertEquals(1, (long) accountService.getAccountList().size());
        Assertions.assertEquals(2, (long) accountService.getAccountList().get(0).getStatements().size());
        Assertions.assertEquals(300, accountService.getAccountList().get(0).getCurrentBalance());
    }

    @Test
    public void shouldGetAccountStatement() {
        List<Statement> statements = new ArrayList<>();
        statements.add(new Statement("11/11/2020", 1000, 1000));
        statements.add(new Statement("11/11/2021", -500, 500));
        accountService.getAccountList().add(Account.builder()
                .id(1)
                .currentBalance(500)
                .statements(statements)
                .build());

        AccountStatementRequest request = AccountStatementRequest.builder()
                .accountId(1)
                .periodType(PeriodTypeEnum.YEAR.name())
                .year(2021)
                .build();

        ResponseEntity<List<AccountStatementResponse>> response = accountController.getAccountStatement(request);

        Assertions.assertEquals(1, (long) Objects.requireNonNull(response.getBody()).size());
        Assertions.assertEquals(500, response.getBody().get(0).getDebit());
    }
}
