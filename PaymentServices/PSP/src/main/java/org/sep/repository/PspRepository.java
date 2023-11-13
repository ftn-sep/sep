package org.sep.repository;

import org.sep.model.PaymentData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PspRepository extends JpaRepository<PaymentData, Long> {
}
