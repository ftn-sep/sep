package org.crypto.repository;

import org.crypto.model.Payment;
import org.sep.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findPaymentByMerchantOrderId(Long merchantOrderId);
    Optional<Payment> findPaymentByCoinGateOrderId(Long coingateId);

    Optional<List<Payment>> findAllByStatus(PaymentStatus status);
}
