package org.sep.exceptions;

@SuppressWarnings("serial")
public class FailedPaymentException extends RuntimeException {

    public FailedPaymentException() {
    }

    public FailedPaymentException(String message) {
        super(message);
    }
}