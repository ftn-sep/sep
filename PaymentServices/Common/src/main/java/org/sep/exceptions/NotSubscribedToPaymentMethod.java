package org.sep.exceptions;

import org.sep.enums.PaymentMethod;

@SuppressWarnings("serial")
public class NotSubscribedToPaymentMethod extends RuntimeException {

    public NotSubscribedToPaymentMethod(Long sellerId, PaymentMethod method) {
        super("Seller with id: " + sellerId + " is not subscribed to payment method: " + method);
    }
}