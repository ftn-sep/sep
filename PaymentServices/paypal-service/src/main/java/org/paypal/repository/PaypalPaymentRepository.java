package org.paypal.repository;

import org.paypal.model.PaypalPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaypalPaymentRepository extends JpaRepository<PaypalPayment, Long> {
    PaypalPayment findByPaypalOrderId(String paypalOrderId);
}
