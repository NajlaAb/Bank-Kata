package com.bank.katabank.exception;

/**
 * Thrown to indicate that a given input is invalid
 */
public class InvalidInputException extends RuntimeException {

    public InvalidInputException(String message) {
        super(message);
    }
}
