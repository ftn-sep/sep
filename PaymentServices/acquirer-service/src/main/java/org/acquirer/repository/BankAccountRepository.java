package org.acquirer.repository;

import org.acquirer.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    public Optional<BankAccount> findByMerchantId(String merchantId);

    public Optional<BankAccount> findByCardPan(String pan);

    Optional<BankAccount> findByAccountNumber(String accountNumber);
}
