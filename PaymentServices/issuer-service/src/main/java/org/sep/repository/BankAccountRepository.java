package org.sep.repository;

import org.sep.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    public Optional<BankAccount> findByCardPan(String pan);
}
