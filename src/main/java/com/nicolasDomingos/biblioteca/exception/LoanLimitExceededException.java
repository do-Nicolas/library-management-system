package com.nicolasDomingos.biblioteca.exception;

public class LoanLimitExceededException extends Exception {
    public LoanLimitExceededException(String message) { super(message); }
}
