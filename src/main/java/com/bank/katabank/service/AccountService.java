package com.bank.katabank.service;

import com.bank.katabank.dto.AccountStatementRequest;
import com.bank.katabank.dto.AccountStatementResponse;
import com.bank.katabank.dto.DepositRequest;
import com.bank.katabank.dto.WithdrawRequest;
import com.bank.katabank.exception.InsufficientBalanceException;
import com.bank.katabank.exception.InvalidInputException;

import java.util.List;

public interface AccountService {

    /**
     * Deposit Money
     * @param request Deposit Request
     * @throws InvalidInputException
     */
    void deposit(DepositRequest request) throws InvalidInputException;

    /**
     * Withdraw Money
     * @param request Withdraw Request
     * @throws InvalidInputException
     * @throws InsufficientBalanceException
     */
    void withdraw(WithdrawRequest request) throws InvalidInputException, InsufficientBalanceException;

    /**
     * Get Account Statement for a given year, a given month or for a custom period of time
     * @param request Account Statement Request
     * @return
     */
    List<AccountStatementResponse> getAccountStatement(AccountStatementRequest request);
}
