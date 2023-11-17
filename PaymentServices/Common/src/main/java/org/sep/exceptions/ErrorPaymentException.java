package org.sep.exceptions;

@SuppressWarnings("serial")
public class ErrorPaymentException extends RuntimeException {

    public ErrorPaymentException() {
    }

    public ErrorPaymentException(String message) {
        super(message);
    }
}