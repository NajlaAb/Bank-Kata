package com.bank.katabank.exception;

/**
 * Thrown to indicate that your account does not contain
 * enough money to perform the current action
 */
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String message) {
        super(message);
    }

}
