package org.crypto.repository;

import org.crypto.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findPaymentByCoinGateOrderId(String orderId);

    Optional<Payment> findPaymentByMerchantOrderId(Long merchantOrderId);
}
