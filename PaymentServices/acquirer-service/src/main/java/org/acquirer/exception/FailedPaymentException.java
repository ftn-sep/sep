package org.acquirer.exception;

@SuppressWarnings("serial")
public class FailedPaymentException extends RuntimeException {

    public FailedPaymentException() {
    }

    public FailedPaymentException(String message) {
        super(message);
    }
}