package org.acquirer.exception;

@SuppressWarnings("serial")
public class ErrorPaymentException extends RuntimeException {

    public ErrorPaymentException() {
    }

    public ErrorPaymentException(String message) {
        super(message);
    }
}