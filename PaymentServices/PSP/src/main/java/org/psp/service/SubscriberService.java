package org.psp.service;

import org.psp.model.Seller;
import org.sep.enums.PaymentMethod;
import org.sep.exceptions.NotSubscribedToPaymentMethod;
import org.springframework.stereotype.Service;

@Service
public class SubscriberService {

    public static void checkIfSellerIsSubscribedToMethod(Seller seller, PaymentMethod method) throws NotSubscribedToPaymentMethod {
        if (!seller.getAvailablePaymentMethods().contains(method)) {
            throw new NotSubscribedToPaymentMethod(seller.getSellerId(), method);
        }
    }
}
