package com.bank.katabank.service.stepdefinitions;

import com.bank.katabank.service.impl.AccountServiceImpl;

public class RootStep {
    protected static AccountServiceImpl accountService;

    /**
     * Account List Size
     */
    protected int accountListSize;

    /**
     * Account List Size
     */
    protected int statementListSize;

    public RootStep() {
        initialize();
    }

    public void initialize() {
        if (accountService == null)
            accountService = new AccountServiceImpl();
    }
}
