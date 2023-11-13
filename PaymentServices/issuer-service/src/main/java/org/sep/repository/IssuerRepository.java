package org.sep.repository;

import org.sep.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuerRepository extends JpaRepository<BankAccount, Long> {
}
