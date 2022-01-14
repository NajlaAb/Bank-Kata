package com.bank.katabank.service.impl;

import com.bank.katabank.dto.AccountStatementRequest;
import com.bank.katabank.dto.AccountStatementResponse;
import com.bank.katabank.dto.DepositRequest;
import com.bank.katabank.dto.WithdrawRequest;
import com.bank.katabank.exception.InsufficientBalanceException;
import com.bank.katabank.exception.InvalidInputException;
import com.bank.katabank.model.Account;
import com.bank.katabank.model.Statement;
import com.bank.katabank.model.enumeratin.MonthEnum;
import com.bank.katabank.model.enumeratin.PeriodTypeEnum;
import com.bank.katabank.service.AccountService;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.bank.katabank.utils.MessageUtils.DATE_PATTERN;
import static com.bank.katabank.utils.MessageUtils.FORMAT_DATE_NOT_VALID;
import static com.bank.katabank.utils.MessageUtils.MONTH_NOT_PROVIDED;
import static com.bank.katabank.utils.MessageUtils.MONTH_NOT_VALID;
import static com.bank.katabank.utils.MessageUtils.NO_NEGATIVE_AMOUNT_TO_DEPOSIT;
import static com.bank.katabank.utils.MessageUtils.NO_NEGATIVE_AMOUNT_TO_WITHDRAWAL;
import static com.bank.katabank.utils.MessageUtils.NO_SUFFICIENT_BALANCE;
import static com.bank.katabank.utils.MessageUtils.PERIOD_TYPE_NOT_VALID;
import static com.bank.katabank.utils.MessageUtils.START_END_DATE_NOT_PROVIDED;
import static com.bank.katabank.utils.MessageUtils.START_END_DATE_NOT_VALID;
import static com.bank.katabank.utils.MessageUtils.YEAR_NOT_PROVIDED;

@Service
public class AccountServiceImpl implements AccountService {

    private List<Account> accountList = new ArrayList<>();

    public List<Account> getAccountList() {
        return accountList;
    }

    /**
     * Find Account By ID
     * @param id Account ID
     * @return Account
     */
    private Optional<Account> findAccountById(int id) {
        return this.accountList.stream().filter(account -> id == account.getId()).findFirst();
    }

    @Override
    public void deposit(DepositRequest request) throws InvalidInputException {
        if (request.getAmount() < 0) {
            throw new InvalidInputException(NO_NEGATIVE_AMOUNT_TO_DEPOSIT);
        }
        Optional<Account> accountById = findAccountById(request.getAccountId());
        if (accountById.isPresent()) {
            accountById.get().deposit(request.getAmount());
        } else {
            Account newAccount = Account.builder().id(request.getAccountId()).statements(new ArrayList<>()).build();
            newAccount.deposit(request.getAmount());
            accountList.add(newAccount);
        }
    }

    @Override
    public void withdraw(WithdrawRequest request) throws InvalidInputException, InsufficientBalanceException {
        if (request.getAmount() < 0 ) {
            throw new InvalidInputException(NO_NEGATIVE_AMOUNT_TO_WITHDRAWAL);
        }
        Optional<Account> account = findAccountById(request.getAccountId());
        if (!account.isPresent() || account.get().getCurrentBalance() < request.getAmount()) {
            throw new InsufficientBalanceException(NO_SUFFICIENT_BALANCE);
        }
        account.get().withdraw(request.getAmount());
    }

    @Override
    public List<AccountStatementResponse> getAccountStatement(AccountStatementRequest request) {
        List<AccountStatementResponse> response = new ArrayList<>();
        Date startDate = null;
        Date endDate = null;
        Optional<Account> account = findAccountById(request.getAccountId());
        if (!account.isPresent()) {
            return response;
        }
        if (StringUtils.isEmpty(request.getPeriodType())) {
            response.addAll(getFinalStatementList(account.get(), statement -> true));
            return response;
        }
        if(!EnumUtils.isValidEnum(PeriodTypeEnum.class, request.getPeriodType().toUpperCase())) {
            throw new InvalidInputException(PERIOD_TYPE_NOT_VALID);
        }
        if (!StringUtils.isEmpty(request.getStartDate())) {
            startDate = getDate(request.getStartDate());
        }
        if (!StringUtils.isEmpty(request.getEndDate())) {
            endDate = getDate(request.getEndDate());
        }
        filterStatements(request, response, startDate, endDate, account.get());
        return response;
    }

    /**
     * Filter Statements By Criteria Defined In Request
     * @param request       Account Statement Request
     * @param response      Account Statement Response
     * @param startDate     Start Date
     * @param endDate       End Date
     * @param account       User Account
     */
    private void filterStatements(AccountStatementRequest request, List<AccountStatementResponse> response, Date startDate,
                                 Date endDate, Account account) {
        switch (PeriodTypeEnum.valueOf(request.getPeriodType().toUpperCase())) {
            case YEAR:
                if (request.getYear() == 0) {
                    throw new InvalidInputException(YEAR_NOT_PROVIDED);
                }
                response.addAll(getFinalStatementList(account, filterStatementByYear(request.getYear())));
                break;
            case MONTH:
                String month = checkMonth(request.getMonth());
                response.addAll(getFinalStatementList(account, filterStatementByMonth(month)));
                break;
            case CUSTOM:
                if (startDate == null || endDate == null) {
                    throw new InvalidInputException(START_END_DATE_NOT_PROVIDED);
                }
                if (startDate.after(endDate)) {
                    throw new InvalidInputException(START_END_DATE_NOT_VALID);
                }
                response.addAll(getFinalStatementList(account, filterStatementByDate(startDate, endDate)));
            break;
        }
    }

    /**
     * Check Month
     * @param month Month To Check
     * @return Month
     */
    private String checkMonth(String month) {
        if (StringUtils.isEmpty(month)) {
            throw new InvalidInputException(MONTH_NOT_PROVIDED);
        }
        Optional<String> monthInLetters = Arrays.stream(MonthEnum.values())
                .filter(monthEnum -> monthEnum.name().equals(month.toUpperCase())
                        || monthEnum.getValue().equals(month))
                .map(Enum::name).findFirst();
        if (!monthInLetters.isPresent()) {
            throw new InvalidInputException(MONTH_NOT_VALID);
        }
        return monthInLetters.get();
    }

    /**
     * Get Final Statement List
     * @param account   User Account
     * @param statementPredicate    Statement Predicate
     * @return  {@link List} of {@link AccountStatementResponse}
     */
    private List<AccountStatementResponse> getFinalStatementList(Account account, Predicate<Statement> statementPredicate) {
        return account.getStatements().stream().filter(statementPredicate
        ).map(statement -> statement.getAmount() > 0 ?
                new AccountStatementResponse(statement.getDate(), statement.getAmount(),
                        0, statement.getBalance()) :
                new AccountStatementResponse(statement.getDate(), 0,
                        Math.abs(statement.getAmount()), statement.getBalance())

        ).collect(Collectors.toList());
    }

    /**
     * Filter Statements By Month
     * @param month Month To Filter By
     * @return  Statement Predicate
     */
    private Predicate<Statement> filterStatementByMonth(String month) {
        return statement ->
                LocalDate.parse(statement.getDate(),
                        DateTimeFormatter.ofPattern(DATE_PATTERN)).getMonth().toString().equals(month);
    }

    /**
     * Filter Statement By Year
     * @param year  Year To Filter By
     * @return Statement Predicate
     */
    private Predicate<Statement> filterStatementByYear(int year) {
        return statement ->
                LocalDate.parse(statement.getDate(),
                        DateTimeFormatter.ofPattern(DATE_PATTERN)).getYear() == year;
    }

    /**
     * Filter Statements By Start Date And End Date
     * @param startDate    Start Date
     * @param endDate      End Date
     * @return Statement Predicate
     */
    private Predicate<Statement> filterStatementByDate(Date startDate, Date endDate) {
        return statement ->
        {
            try {
                return new SimpleDateFormat(DATE_PATTERN).parse(statement.getDate()).after(startDate)
                        && new SimpleDateFormat(DATE_PATTERN).parse(statement.getDate()).before(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return false;
        };
    }

    /**
     * Get Date From Given String Value
     * @param date String Value of Date
     * @return Date
     */
    private Date getDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            throw new InvalidInputException(FORMAT_DATE_NOT_VALID + DATE_PATTERN);
        }
    }
}
