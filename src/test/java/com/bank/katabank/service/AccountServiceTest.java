package com.bank.katabank.service;

import com.bank.katabank.dto.AccountStatementRequest;
import com.bank.katabank.dto.AccountStatementResponse;
import com.bank.katabank.dto.DepositRequest;
import com.bank.katabank.dto.WithdrawRequest;
import com.bank.katabank.exception.InsufficientBalanceException;
import com.bank.katabank.exception.InvalidInputException;
import com.bank.katabank.model.Account;
import com.bank.katabank.model.Statement;
import com.bank.katabank.model.enumeratin.PeriodTypeEnum;
import com.bank.katabank.service.impl.AccountServiceImpl;
import com.bank.katabank.utils.MessageUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bank.katabank.utils.MessageUtils.DATE_PATTERN;
import static com.bank.katabank.utils.MessageUtils.FORMAT_DATE_NOT_VALID;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AccountServiceTest {

    @Autowired
    private AccountServiceImpl accountService;

    @BeforeEach
    public void initEach(){
        accountService.getAccountList().clear();
    }

    @Test
    void shouldNotDepositWhenNegativeAmount() {
        DepositRequest request = DepositRequest.builder().accountId(1).amount(-1200).build();

        InvalidInputException thrown = Assertions.assertThrows(InvalidInputException.class, () -> {
            accountService.deposit(request);
        }, MessageUtils.NO_NEGATIVE_AMOUNT_TO_DEPOSIT);

        Assertions.assertEquals(MessageUtils.NO_NEGATIVE_AMOUNT_TO_DEPOSIT, thrown.getMessage());
    }

    @Test
    void shouldDepositAmountWhenAccountIsPresent() throws InvalidInputException {
        DepositRequest request = DepositRequest.builder().accountId(1).amount(1200).build();
        List<Statement> statements = new ArrayList<>();
        statements.add(new Statement(
                new SimpleDateFormat(MessageUtils.DATE_PATTERN).format(new Date()), 1000, 1000
        ));
        accountService.getAccountList().add(Account.builder()
                .id(1)
                .currentBalance(1000)
                .statements(statements)
                .build());

        accountService.deposit(request);

        Assertions.assertEquals(1, (long) accountService.getAccountList().size());
        Assertions.assertEquals(2, (long) accountService.getAccountList().get(0).getStatements().size());
        Assertions.assertEquals(2200, accountService.getAccountList().get(0).getCurrentBalance());
    }

    @Test
    void shouldDepositAmountWhenAccountNotPresent() throws InvalidInputException {
        DepositRequest request = DepositRequest.builder().accountId(1).amount(1200).build();

        accountService.deposit(request);

        Assertions.assertEquals(1, (long) accountService.getAccountList().size());
        Assertions.assertEquals(1, (long) accountService.getAccountList().get(0).getStatements().size());
        Assertions.assertEquals(1200, accountService.getAccountList().get(0).getCurrentBalance());
    }

    @Test
    void shouldNotWithdrawalNegativeAmount() {
        WithdrawRequest request = WithdrawRequest.builder().accountId(1).amount(-1200).build();

        InvalidInputException thrown = Assertions.assertThrows(InvalidInputException.class, () -> {
            accountService.withdraw(request);
        }, MessageUtils.NO_NEGATIVE_AMOUNT_TO_WITHDRAWAL);

        Assertions.assertEquals(MessageUtils.NO_NEGATIVE_AMOUNT_TO_WITHDRAWAL, thrown.getMessage());
    }

    @Test
    void shouldNotWithdrawalWhenInsufficientBalance() {
        WithdrawRequest request = WithdrawRequest.builder().accountId(1).amount(11200).build();

        InsufficientBalanceException thrown = Assertions.assertThrows(InsufficientBalanceException.class, () -> {
            accountService.withdraw(request);
        }, MessageUtils.NO_SUFFICIENT_BALANCE);

        Assertions.assertEquals(MessageUtils.NO_SUFFICIENT_BALANCE, thrown.getMessage());
    }

    @Test
    void shouldWithdrawMoney() throws InvalidInputException {
        WithdrawRequest request = WithdrawRequest.builder().accountId(1).amount(700).build();
        List<Statement> statements = new ArrayList<>();
        statements.add(new Statement(
                new SimpleDateFormat(MessageUtils.DATE_PATTERN).format(new Date()), 1000, 1000
        ));
        accountService.getAccountList().add(Account.builder()
                .id(1)
                .currentBalance(1000)
                .statements(statements)
                .build());

        accountService.withdraw(request);

        Assertions.assertEquals(1, (long) accountService.getAccountList().size());
        Assertions.assertEquals(2, (long) accountService.getAccountList().get(0).getStatements().size());
        Assertions.assertEquals(300, accountService.getAccountList().get(0).getCurrentBalance());
    }

    @Test
    void shouldGetEmptyAccountStatementWhenAccountNotFound() {
        AccountStatementRequest request = AccountStatementRequest.builder().accountId(1).build();

        List<AccountStatementResponse> response = accountService.getAccountStatement(request);

        Assertions.assertEquals(0, (long) response.size());
    }

    @Test
    void shouldGetAllAccountStatementWhenPeriodTypeNotDefined() {
        List<Statement> statements = new ArrayList<>();
        statements.add(new Statement(
                new SimpleDateFormat(MessageUtils.DATE_PATTERN).format(new Date()), 1000, 1000
        ));
        statements.add(new Statement(
                new SimpleDateFormat(MessageUtils.DATE_PATTERN).format(new Date()), -500, 500
        ));
        accountService.getAccountList().add(Account.builder()
                .id(1)
                .currentBalance(500)
                .statements(statements)
                .build());
        AccountStatementRequest request = AccountStatementRequest.builder().accountId(1).build();

        List<AccountStatementResponse> response = accountService.getAccountStatement(request);

        Assertions.assertEquals(2, (long) response.size());
        Assertions.assertEquals(500, response.get(1).getDebit());
    }

    @Test
    void shouldNotGetAccountStatementWhenPeriodTypeNotValid() {
        AccountStatementRequest request = AccountStatementRequest.builder()
                .accountId(1)
                .periodType(MessageUtils.EMPTY_VALUE)
                .build();

        List<Statement> statements = new ArrayList<>();
        statements.add(new Statement(
                new SimpleDateFormat(MessageUtils.DATE_PATTERN).format(new Date()), 1000, 1000
        ));
        accountService.getAccountList().add(Account.builder()
                .id(1)
                .currentBalance(1000)
                .statements(statements)
                .build());

        InvalidInputException thrown = Assertions.assertThrows(InvalidInputException.class, () -> {
            accountService.getAccountStatement(request);
        }, MessageUtils.PERIOD_TYPE_NOT_VALID);

        Assertions.assertEquals(MessageUtils.PERIOD_TYPE_NOT_VALID, thrown.getMessage());
    }

    @Test
    void shouldNotGetAmountStatementWhenPeriodTypeIsYearAndYearNotDefined() {
        AccountStatementRequest request = AccountStatementRequest.builder()
                .accountId(1)
                .periodType(PeriodTypeEnum.YEAR.name())
                .build();

        List<Statement> statements = new ArrayList<>();
        statements.add(new Statement(
                new SimpleDateFormat(MessageUtils.DATE_PATTERN).format(new Date()), 1000, 1000
        ));
        accountService.getAccountList().add(Account.builder()
                .id(1)
                .currentBalance(1000)
                .statements(statements)
                .build());

        InvalidInputException thrown = Assertions.assertThrows(InvalidInputException.class, () -> {
            accountService.getAccountStatement(request);
        }, MessageUtils.YEAR_NOT_PROVIDED);

        Assertions.assertEquals(MessageUtils.YEAR_NOT_PROVIDED, thrown.getMessage());
    }

    @Test
    void shouldGetAmountStatementFilteredByYear() {
        AccountStatementRequest request = AccountStatementRequest.builder()
                .accountId(1)
                .periodType(PeriodTypeEnum.YEAR.name())
                .year(2021)
                .build();

        List<Statement> statements = new ArrayList<>();
        statements.add(new Statement("11/11/2020", 1000, 1000));
        statements.add(new Statement("11/11/2021", -500, 500));
        accountService.getAccountList().add(Account.builder()
                .id(1)
                .currentBalance(500)
                .statements(statements)
                .build());

        List<AccountStatementResponse> response = accountService.getAccountStatement(request);

        Assertions.assertEquals(1, (long) response.size());
        Assertions.assertEquals(500, response.get(0).getDebit());
    }

    @Test
    void shouldNotGetAmountStatementWhenPeriodTypeIsMonthAndMonthNotDefined() {
        AccountStatementRequest request = AccountStatementRequest.builder()
                .accountId(1)
                .periodType(PeriodTypeEnum.MONTH.name())
                .build();

        List<Statement> statements = new ArrayList<>();
        statements.add(new Statement(
                new SimpleDateFormat(MessageUtils.DATE_PATTERN).format(new Date()), 1000, 1000
        ));
        accountService.getAccountList().add(Account.builder()
                .id(1)
                .currentBalance(1000)
                .statements(statements)
                .build());

        InvalidInputException thrown = Assertions.assertThrows(InvalidInputException.class, () -> {
            accountService.getAccountStatement(request);
        }, MessageUtils.MONTH_NOT_PROVIDED);

        Assertions.assertEquals(MessageUtils.MONTH_NOT_PROVIDED, thrown.getMessage());
    }

    @Test
    void shouldNotGetAmountStatementWhenPeriodTypeIsMonthAndMonthNotValid() {
        AccountStatementRequest request = AccountStatementRequest.builder()
                .accountId(1)
                .periodType(PeriodTypeEnum.MONTH.name())
                .month("16")
                .build();

        List<Statement> statements = new ArrayList<>();
        statements.add(new Statement(
                new SimpleDateFormat(MessageUtils.DATE_PATTERN).format(new Date()), 1000, 1000
        ));
        accountService.getAccountList().add(Account.builder()
                .id(1)
                .currentBalance(1000)
                .statements(statements)
                .build());

        InvalidInputException thrown = Assertions.assertThrows(InvalidInputException.class, () -> {
            accountService.getAccountStatement(request);
        }, MessageUtils.MONTH_NOT_VALID);

        Assertions.assertEquals(MessageUtils.MONTH_NOT_VALID, thrown.getMessage());
    }

    @Test
    void shouldGetAmountStatementFilteredByMonth() {
        AccountStatementRequest request = AccountStatementRequest.builder()
                .accountId(1)
                .periodType(PeriodTypeEnum.MONTH.name())
                .month("11")
                .build();

        List<Statement> statements = new ArrayList<>();
        statements.add(new Statement("11/10/2021", 1000, 1000));
        statements.add(new Statement("20/10/2021", 250, 1250));
        statements.add(new Statement("11/11/2021", -500, 750));
        accountService.getAccountList().add(Account.builder()
                .id(1)
                .currentBalance(750)
                .statements(statements)
                .build());

        List<AccountStatementResponse> response = accountService.getAccountStatement(request);

        Assertions.assertEquals(1, (long) response.size());
        Assertions.assertEquals(750, response.get(0).getCurrentBalance());
    }

    @Test
    void shouldNotGetAmountStatementWhenPeriodTypeIsCustomAndStartDateNotValid() {
        AccountStatementRequest request = AccountStatementRequest.builder()
                .accountId(1)
                .periodType(PeriodTypeEnum.CUSTOM.name())
                .startDate("11.11.2021")
                .build();

        List<Statement> statements = new ArrayList<>();
        statements.add(new Statement(
                new SimpleDateFormat(MessageUtils.DATE_PATTERN).format(new Date()), 1000, 1000
        ));
        accountService.getAccountList().add(Account.builder()
                .id(1)
                .currentBalance(1000)
                .statements(statements)
                .build());

        InvalidInputException thrown = Assertions.assertThrows(InvalidInputException.class, () -> {
            accountService.getAccountStatement(request);
        }, FORMAT_DATE_NOT_VALID + DATE_PATTERN);

        Assertions.assertEquals(FORMAT_DATE_NOT_VALID + DATE_PATTERN, thrown.getMessage());
    }

    @Test
    void shouldNotGetAmountStatementWhenPeriodTypeIsCustomAndStartDateNotProvided() {
        AccountStatementRequest request = AccountStatementRequest.builder()
                .accountId(1)
                .periodType(PeriodTypeEnum.CUSTOM.name())
                .build();

        List<Statement> statements = new ArrayList<>();
        statements.add(new Statement(
                new SimpleDateFormat(MessageUtils.DATE_PATTERN).format(new Date()), 1000, 1000
        ));
        accountService.getAccountList().add(Account.builder()
                .id(1)
                .currentBalance(1000)
                .statements(statements)
                .build());

        InvalidInputException thrown = Assertions.assertThrows(InvalidInputException.class, () -> {
            accountService.getAccountStatement(request);
        }, MessageUtils.START_END_DATE_NOT_PROVIDED);

        Assertions.assertEquals(MessageUtils.START_END_DATE_NOT_PROVIDED, thrown.getMessage());
    }

    @Test
    void shouldNotGetAmountStatementWhenPeriodTypeIsCustomAndStartDateAfterEndDate() {
        AccountStatementRequest request = AccountStatementRequest.builder()
                .accountId(1)
                .periodType(PeriodTypeEnum.CUSTOM.name())
                .startDate("12/11/2021")
                .endDate("02/11/2021")
                .build();

        List<Statement> statements = new ArrayList<>();
        statements.add(new Statement(
                new SimpleDateFormat(MessageUtils.DATE_PATTERN).format(new Date()), 1000, 1000
        ));
        accountService.getAccountList().add(Account.builder()
                .id(1)
                .currentBalance(1000)
                .statements(statements)
                .build());

        InvalidInputException thrown = Assertions.assertThrows(InvalidInputException.class, () -> {
            accountService.getAccountStatement(request);
        }, MessageUtils.START_END_DATE_NOT_VALID);

        Assertions.assertEquals(MessageUtils.START_END_DATE_NOT_VALID, thrown.getMessage());
    }

    @Test
    void shouldGetAmountStatement() {
        AccountStatementRequest request = AccountStatementRequest.builder()
                .accountId(1)
                .periodType(PeriodTypeEnum.CUSTOM.name())
                .startDate("12/11/2021")
                .endDate("22/11/2021")
                .build();

        List<Statement> statements = new ArrayList<>();
        statements.add(new Statement("13/11/2021", 1000, 1000));
        statements.add(new Statement("14/11/2021", 250, 1250));
        statements.add(new Statement("24/11/2021", -500, 750));
        accountService.getAccountList().add(Account.builder()
                .id(1)
                .currentBalance(750)
                .statements(statements)
                .build());

        List<AccountStatementResponse> response = accountService.getAccountStatement(request);

        Assertions.assertEquals(2, (long) response.size());
        Assertions.assertEquals(1250, response.get(1).getCurrentBalance());
    }
}
